package cn.et;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController{
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired  
	private LoadBalancerClient loadBalancer; 
    @ResponseBody
	@GetMapping("/choosePub")  
    public String choosePub() {  
        StringBuffer sb=new StringBuffer();  
        for(int i=0;i<=10;i++) {
            ServiceInstance ss=loadBalancer.choose("EUREKAMAIL");//从两个idserver中选择一个 这里涉及到选择算法  
            sb.append(ss.getUri().toString()+"<br/>");  
        }
        return sb.toString();  
    }
	
    @GetMapping(value="/hello")
    public String hello(String email_to,String email_subject,String email_content) {
    	String controller="mail";
        try {
        	controller+="?email_to="+email_to+"&email_subject="+email_subject+"&email_content="+email_content;
			String result =  restTemplate.getForObject("http://eurekamail/"+controller, String.class);
		} catch (RestClientException e) {
			e.printStackTrace();
			return "redirect:/error.html";
		}
        
        return "/suc.html";
    }
    
    @GetMapping(value="/sends")
    public String sends(String email_to,String email_subject,String email_content) {
    	//String controller="/mails";
        try {
        	//controller+="?email_to="+email_to+"&email_subject="+email_subject+"&email_content="+email_content;
			//String result =  restTemplate.getForObject("http://eureka-provider/"+controller, String.class);
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);  
        	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); 
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("email_to",email_to);
        	map.put("email_subject",email_subject);     	
        	map.put("email_content",email_content);
        	HttpEntity<Map> request = new HttpEntity<Map>(map,headers);
        	restTemplate.postForEntity("http://EUREKAMAIL/sendMail", request, String.class);
		} catch (RestClientException e) {
			e.printStackTrace();
			return "redirect:/error.html";
		}
        return "redirect:/suc.html";
    }
    @GetMapping(value="/uservake")
    public String uservake(String userid){
    	String result =  restTemplate.getForObject("http://eurekamail/user/{userid}", String.class,userid);
    	return result;
    }
}
