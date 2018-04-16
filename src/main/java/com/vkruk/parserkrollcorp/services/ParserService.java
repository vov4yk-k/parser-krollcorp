package com.vkruk.parserkrollcorp.services;

import com.vkruk.parserkrollcorp.entity.Parsing;
import com.vkruk.parserkrollcorp.entity.Product;
import com.vkruk.parserkrollcorp.model.ProductInfo;
import com.vkruk.parserkrollcorp.entity.ProductLink;
import com.vkruk.parserkrollcorp.entity.ProductProperty;
import com.vkruk.parserkrollcorp.repository.ParsingRepository;
import com.vkruk.parserkrollcorp.repository.ProductLinkRepository;
import com.vkruk.parserkrollcorp.repository.ProductRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;


@Service
public class ParserService {

    private final ProductLinkRepository linkRepository;

    private final ProductRepository productRepository;

    private final ParsingRepository parsingRepository;

    private final ParsingInfoService infoService;

    private static final Logger logger = LogManager.getLogger(ParserService.class);

    private HashMap<String, ArrayList<ProductProperty>> properties;

    private String cookie;

    private Environment env;


    @Autowired
    public ParserService(ProductLinkRepository linkRepository,ProductRepository productRepository,
                         ParsingRepository parsingRepository, Environment env, ParsingInfoService infoService) {
        this.linkRepository = linkRepository;
        this.productRepository = productRepository;
        this.parsingRepository = parsingRepository;
        this.infoService = infoService;
        this.env = env;
        signIn();
    }

    public void signIn(){

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("email", env.getProperty("site.login"));
        map.add("password", env.getProperty("site.password"));

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity(map, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange("https://www.krollcorp.com/signin", HttpMethod.POST, requestEntity,String.class);

        this.cookie = "";
        response.getHeaders().get("Set-Cookie").forEach( currentCookie ->{
            this.cookie = this.cookie.isEmpty() ? currentCookie : this.cookie + "; " + currentCookie;
        });

    }

    @Async
    public void parseLinksAndProducts(long parsingId, String[] paramsArray) {

        try {

            infoService.inProgress(parsingId, "Started!");

            for (String params : paramsArray) {
                parseLinksByParameters(parsingId, params);
            }

            infoService.inProgress(parsingId, "Parsed "+linkRepository.getProductLinksByParseId(parsingId).size()+" links!");

            parseProductsByParsingId(parsingId);

        }catch (Exception e){
            infoService.error(parsingId, "Parsing fail!");
            infoService.error(parsingId, e.getMessage());
        }

        int productsQty = productRepository.getProductsByParseId(parsingId).size();
        double duration = infoService.minutesForAllCurrentOperations(parsingId);
        infoService.finished(parsingId, "Parsed "+productsQty
                +" products in "+String.format("%.2f",duration)+" minutes!"
                +" Average speed "+String.format("%.2f",duration*60/productsQty)+" sec/product." );

    }

    public void parseLinksByParameters(long parsingId, String params){

        ArrayList<ProductLink> productLinks = new ArrayList<>();

        int pageNumber = 1;
        boolean parseNextPage = true;
        while(parseNextPage){
            try {
                parseNextPage = parsePage(100, pageNumber, productLinks, params);
                pageNumber++;
            } catch (IOException e) {
                infoService.error(parsingId,"Error on page "+pageNumber);
                infoService.error(parsingId, e.getMessage());
                parseNextPage = false;
            }
        }

        productLinks.forEach((productLink) -> {
            List<ProductLink> storedLinks = linkRepository.findProductLinksByProductSKU(productLink.getProductSKU());
            if (storedLinks.isEmpty()){
                linkRepository.save(productLink);
            }
            parsingRepository.save(new Parsing(parsingId, productLink.getProductSKU()));
        });

        infoService.inProgress(parsingId, "Parsed "+productLinks.size()+" links with parameters "+params);

        productLinks.clear();

    }

    public void parseProductsByParsingId(long parsingId){

        List <ProductLink> productLinks = linkRepository.getProductLinksByParseId(parsingId);
        HashMap<String, ProductLink> links = new HashMap<String, ProductLink>();
        productLinks.forEach(productLink -> {
            //if(productRepository.findProductsBySku(productLink.getProductSKU()).isEmpty()){
                links.put(productLink.getProductSKU(), productLink);
            //}
        });

        Map<String, String> cookies = new HashMap<>();
        cookies.put("Cookie", this.cookie);

        infoService.inProgress(parsingId, "Product links parsing");


        int linksCount = 0;
        HashSet<String> parsedSKUs = new HashSet<>();

        Iterator<Map.Entry<String, ProductLink>> iterator = links.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<String, ProductLink> entry = iterator.next();
            String sku = entry.getKey();
            ProductLink productLink = entry.getValue();


            if(parsedSKUs.contains(sku)){
                linksCount++;
                infoService.inProgressUpdate(parsingId, "Parsing products - " + String.format ("%.2f", ((double) linksCount/links.size())*100) + "%");
                continue;
            }

            logger.info(sku + " - started");

            Document doc = null;
            try {
                doc = Jsoup.connect("https://www.krollcorp.com" + productLink.getLink())
                        .cookie("Cookie", this.cookie)
                        .timeout(60 * 10000)
                        .get();
            } catch (IOException e) {
                infoService.error(parsingId, "Connection error. Product link " + productLink.getLink());
                infoService.error(parsingId, e.getMessage());
                continue;
            }

            Product parentProduct = getProductFromPage(doc);
            parentProduct.setSku(sku);

            ArrayList<Product> products = new ArrayList<Product>();
            Elements elem = doc.select(".isoption");
            if (elem.size() > 0) {
                products = createProducts(doc);
                products.forEach(newProduct -> { newProduct.fillByParrent(parentProduct); });
            } else {
                products.add(parentProduct);
            }

            products.forEach(newProduct -> {
                parsedSKUs.add(newProduct.getSku());
                if(productRepository.exists(newProduct.getId())){
                    productRepository.delete(newProduct.getId());
                }
            });
            productRepository.save(products);

            logger.info(sku + " - ok");

            linksCount++;
            infoService.inProgressUpdate(parsingId, "Parsing products - " + String.format ("%.2f", ((double) linksCount/links.size())*100) + "%");

        }

    }

    public boolean parsePage(int size, int index, ArrayList<ProductLink> list, String params) throws IOException {

        String address = "https://www.krollcorp.com/facetsearch?PageSize=" + size + "&PageIndex=" + index;

        if (!params.isEmpty()) {
            address = address + "&" + params;
        }

        Document doc = Jsoup.connect(address).timeout(60 * 10000).get();

        logger.info("Page " + index + " loaded");

        Elements products = doc.select(".ProductInfoTable");
        products.forEach(element -> {

            Element description = element.select(".productdescription").select("a").get(0);
            String link = description.attr("href");

            TextNode skuNode = (TextNode) element.select("td:contains(Kroll Sku:)").next().get(1).childNode(0);

            String sku = skuNode.getWholeText();

            list.add(new ProductLink(sku, link));

        });

        return products.size()>0;

    }


    public ProductInfo getProductInfo(String productbvin, String parameters) {

        Set<ProductProperty> properties = new HashSet<>();

        RestTemplate restTemplate = new RestTemplate();
        String body = "productbvin: "+productbvin+"&"+parameters;

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("productbvin" , productbvin);
        String[] par = parameters.split("&");
        for (String p: par){
            String[] curPar = p.split(":");
            String code = curPar[0];
            String valueCode = curPar[1];

            map.add(code, valueCode);
            properties.add(getProductProperty(code,valueCode));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", this.cookie);

        HttpEntity<?> requestEntity = new HttpEntity(map, headers);
        ResponseEntity<ProductInfo> response = restTemplate.exchange("https://www.krollcorp.com/products/validate/"+productbvin, HttpMethod.POST, requestEntity, ProductInfo.class);
        ProductInfo productInfo = response.getBody();

        productInfo.setProperties(properties);

        return  productInfo;
    }

    public ProductProperty getProductProperty(String code, String valueCode){
        ArrayList<ProductProperty> props = properties.get(code);
        ProductProperty pp = props.stream().filter(p -> p.getValueCode().equals(valueCode.trim())).findFirst().get();
        return pp;
    }

    public ArrayList<Product> createProducts(Document productPage) {

        ArrayList<Product> products = new ArrayList<Product>();

        ArrayList<String> listParameters = getParameters(productPage);
        String productbvin = productPage.select("#productbvin").val();

        listParameters.forEach( param -> {

            ProductInfo productInfo = getProductInfo(productbvin, param);

            if(!productInfo.getSku().isEmpty()){
                Product product = new Product(productInfo);
                product.setProductbvin(productbvin);
                products.add(product);
            }

        });

        return products;
    }

    public ArrayList<String>  getParameters(Document doc) {

        this.properties = new HashMap<>();
        Elements options = doc.select(".isoption");
        options.forEach(option -> {
            ArrayList<ProductProperty> listProperties = new ArrayList<>();
            Elements values = option.select("option");
            values.forEach(value -> {
                ProductProperty prop = new ProductProperty(option.attr("id"));
                prop.setProductbvin(doc.select("#productbvin").val());
                prop.setName(getPropertyName(doc,prop.getCode()));
                prop.setValueCode(value.val());
                prop.setValueName(value.text());
                listProperties.add(prop);
            });
            properties.put(option.attr("id"),listProperties);
        });

        ProductProperty[][] arr = new ProductProperty[properties.size()][];
        int i=0;
        for (Map.Entry entry : properties.entrySet()) {

            ArrayList<ProductProperty> props = (ArrayList<ProductProperty>)entry.getValue();
            ProductProperty[] arrayProps = new ProductProperty[props.size()];
            int j = 0;
            for (ProductProperty property : props){
                arrayProps[j] = property;
                j++;
            }
            arr[i] = arrayProps;
            i++;
        }


        ArrayList<String> params = new ArrayList<>();
        for(int index = 0; index<arr.length; index++){
            if(index == 0){
                ProductProperty[] initArray = arr[index];
                for(int initIndex = 0; initIndex < initArray.length; initIndex++){
                    params.add(initArray[initIndex].getParameter());
                }
                continue;
            }

            ArrayList<String> newParams = new ArrayList<>();
            for(int propertyIndex = 0; propertyIndex < params.size(); propertyIndex++){
                String currentPropertyParameter = params.get(propertyIndex);
                ProductProperty[] newPropsArray = arr[index];
                for(int newPropIndex = 0; newPropIndex<newPropsArray.length; newPropIndex++) {
                    ProductProperty newProperty = newPropsArray[newPropIndex];
                    newParams.add(currentPropertyParameter+"&"+newProperty.getParameter());
                }
            }
            params = newParams;

        }

        return params;
    }

    public String getPropertyName(Document productPage, String option) {

        String labelFor = option.substring(3,option.length());
        Elements labels = productPage.select("label");

        for(Element label: labels){
            String propertyCode = label.attributes().get("for");
            if(propertyCode.equals(labelFor)) {
                return label.text();
            }
        }

        return "";
    }

    public Product getProductFromPage(Document doc) {

        long upc = 0;
        String upcStr = "";
        try {
            upcStr = doc.select("#upcCode").text();
            upc = Long.parseLong(upcStr);
        }catch (NumberFormatException e){

        }
        Product product = new Product();
        product.setId(upc);
        product.setUpc(upcStr);
        product.setProductbvin(doc.select("#productbvin").val());
        product.setName(doc.select(".actioncolumn").select("h1").text());
        product.setHazardous(Boolean.parseBoolean(doc.select("#isHazardous").text()));
        product.setManufacturerPartNumber(doc.select("#manufacturerPartNumber").val());
        product.setCountryofOrigin(doc.select("#countryofOrigin").text());
        product.setProductWeight(doc.select("#productWeight").text());
        product.setDescription(doc.select(".productdescription").text());
        product.setImageUrl(doc.select("#imgMain").attr("src"));
        product.setPrice(doc.select("#msrp").text());
        product.setBrand(doc.select(".actioncolumn").select("h2").text());

        Elements groupElements = doc.select(".breadcrumbs").select(".links").select("a");
        if(!groupElements.isEmpty()){
            String subGroup0 = groupElements.get(groupElements.size()-2).text();
            String subGroup1 = groupElements.get(groupElements.size()-1).text();
            product.setProductGroup(subGroup0+" » "+subGroup1);
        }

        return product;

    }

}
