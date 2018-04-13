package com.vkruk.parserkrollcorp.services;

import com.vkruk.parserkrollcorp.entity.Product;
import com.vkruk.parserkrollcorp.model.ProductInfo;
import com.vkruk.parserkrollcorp.entity.ProductLink;
import com.vkruk.parserkrollcorp.entity.ProductProperty;
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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;


@Service
public class ParserService {

    private ProductLinkRepository linkRepository;

    private ProductRepository productRepository;

    private static final Logger logger = LogManager.getLogger(ParserService.class);

    private HashMap<String, ArrayList<ProductProperty>> properties;

    private String cookie;

    private Environment env;


    @Autowired
    public ParserService(ProductLinkRepository linkRepository, ProductRepository productRepository, Environment env) {
        this.linkRepository = linkRepository;
        this.productRepository = productRepository;
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

    public void parseLinksAndProducts(String params) {

        ArrayList<String> parsedSkus = parseLinksByParameters(params);

        parseProductsBySKUs(parsedSkus);

    }

    public ArrayList<String> parseLinksByParameters(String params){

        ArrayList<ProductLink> productLinks = new ArrayList<>();

        int pageNumber = 1;
        boolean parseNextPage = true;
        while(parseNextPage){
            try {
                parseNextPage = parsePage(100, pageNumber, productLinks, params);
                pageNumber++;
            } catch (IOException e) {
                e.printStackTrace();
                parseNextPage = false;
            }
        }

        ArrayList<String> skus = new ArrayList<>();
        productLinks.forEach((productLink) -> {
            List<ProductLink> storedLinks = linkRepository.findProductLinksByProductSKU(productLink.getProductSKU());
            if (storedLinks.isEmpty()){
                linkRepository.save(productLink);
            }
            skus.add(productLink.getProductSKU());
        });

        productLinks.clear();

        return skus;
    }

    public void parseProductsBySKUs(ArrayList<String> skuList){

        Iterable<ProductLink> productLinks = linkRepository.findProductLinksByProductSKUIn(skuList);
        HashMap<String, ProductLink> links = new HashMap<String, ProductLink>();
        productLinks.forEach(productLink -> {
            if(productRepository.findProductsBySku(productLink.getProductSKU()).isEmpty()){
                links.put(productLink.getProductSKU(), productLink);
            }
        });

        Map<String, String> cookies = new HashMap<>();
        cookies.put("Cookie", this.cookie);

        links.forEach((sku, productLink) -> {


            logger.info(sku + " - started");

            Document doc = null;

            try {
                doc = Jsoup.connect("https://www.krollcorp.com" + productLink.getLink())
                        .cookie("Cookie",this.cookie)
                        .timeout(60 * 10000)
                        .get();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Product parentProduct = getProductFromPage(doc);
            parentProduct.setSku(sku);

            Elements elem = doc.select(".isoption");
            if (elem.size() > 0) {
                ArrayList<Product> products = createProductsByParametrs(doc);
                products.forEach(newProduct -> {
                    newProduct.fillByParrent(parentProduct);
                    links.remove(newProduct.getSku());
                });
                productRepository.save(products);
            } else {
                productRepository.save(parentProduct);
                links.remove(parentProduct.getSku());
            }


            logger.info(sku + " - ok");
            return;
        });

    }




    public void parseAll(int firstPage, int lastPage, String manufacturer) {

        ArrayList<ProductLink> productLinks = new ArrayList<>();
        try {
            int i = firstPage;
            while (i < lastPage) {

                logger.info("Start page " + i);
                parsePage(100, i, productLinks, manufacturer);
                logger.info("Stop page " + i + ". Parsed " + productLinks.size());

                logger.info("Start store");
                productLinks.forEach((productLink) -> {
                    linkRepository.save(productLink);
                });
                logger.info("Stored");
                productLinks.clear();

                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean parsePage(int size, int index, ArrayList<ProductLink> list, String manufacturer) throws IOException {

        String address = "https://www.krollcorp.com/facetsearch?PageSize=" + size + "&PageIndex=" + index;

        if (!manufacturer.isEmpty()) {
            address = address + "&f_manufacturer=" + manufacturer;
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

    public void parseLinks() {

        Iterable<ProductLink> productLinks = linkRepository.findAll();
        HashMap<String, ProductLink> links = new HashMap<String, ProductLink>();
        productLinks.forEach(productLink -> {
            if(productRepository.findProductsBySku(productLink.getProductSKU()).isEmpty()){
                links.put(productLink.getProductSKU(), productLink);
            }
        });

        Map<String, String> cookies = new HashMap<>();
        cookies.put("Cookie", this.cookie);

        links.forEach((sku, productLink) -> {


            logger.info(sku + " - started");

            Document doc = null;

            try {
                doc = Jsoup.connect("https://www.krollcorp.com" + productLink.getLink())
                        .cookie("Cookie",this.cookie)
                        .timeout(60 * 10000)
                        .get();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Product parentProduct = getProductFromPage(doc);
            parentProduct.setSku(sku);

            Elements elem = doc.select(".isoption");
            if (elem.size() > 0) {
                ArrayList<Product> products = createProductsByParametrs(doc);
                products.forEach(newProduct -> {
                    newProduct.fillByParrent(parentProduct);
                    links.remove(newProduct.getSku());
                });
                productRepository.save(products);
            } else {
                productRepository.save(parentProduct);
                links.remove(parentProduct.getSku());
            }


            logger.info(sku + " - ok");
            return;
        });

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

    public ArrayList<Product> createProductsByParametrs(Document productPage) {

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

        return product;

    }

}
