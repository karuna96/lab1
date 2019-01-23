package mine;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.ec2.AmazonEC2;

@RestController
public class EC2S3Controller {

	@RequestMapping("/post")
	public void storeObject() {
		//final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard();
		
	}
	
}
