package com.cg.controller;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.LogStream;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.cg.model.Shipwreck;
import com.cg.repository.ShipwreckRepository;


@RestController
@RequestMapping("api/v1/")
public class ShipwreckController {

	@Autowired
	private ShipwreckRepository shipwreckRepositoty;
	public static int k=0;

	@RequestMapping(value="shipwrecks", method = RequestMethod.GET)
	public List<Shipwreck> list(){
		
		
		return shipwreckRepositoty.findAll();
	}
	
	@RequestMapping(value="shipwrecks/{id}", method = RequestMethod.GET)
	public Shipwreck get(@PathVariable long id){
		return shipwreckRepositoty.getOne(id);
	}
	
	@RequestMapping(value="shipwrecks/{id}", method = RequestMethod.PUT)
	public Shipwreck update(@PathVariable long id,@RequestBody Shipwreck wreck){
		Shipwreck shipwreck =shipwreckRepositoty.getOne(id);
		shipwreck=wreck;
		//BeanUtils.copyProperties(wreck, shipwreck);
		shipwreck.setId(id);
		
		return shipwreckRepositoty.saveAndFlush(shipwreck);
		
	}
	
	@RequestMapping(value="shipwrecks/{id}", method = RequestMethod.DELETE)
	public Shipwreck shipwreckDelete(@PathVariable long id){
		Shipwreck existingShipwreck=shipwreckRepositoty.getOne(id);
		shipwreckRepositoty.deleteById(id);
		return existingShipwreck;
	}
	
	@RequestMapping(value="shipwrecks", method = RequestMethod.POST)
	public Shipwreck post(@RequestBody Shipwreck wreck){
//		ClientConfiguration clientConfig = new ClientConfiguration();
//		clientConfig.setProtocol(Protocol.HTTPS);
//		clientConfig.setProxyHost("10.220.1.50");
//		clientConfig.setProxyPort(8080);
//		clientConfig.setMaxConnections(5);
		
		//BasicAWSCredentials cred=new BasicAWSCredentials("AKIAIIKSFWNC3SRSNBRA", "vL5nPlFlpL5PtpNnyTd56ss3jZiFgdq/LS3iqZmY");
		
		AmazonS3Client s3Client = new AmazonS3Client();
		InputLogEvent log = new InputLogEvent();
        log.setMessage("Amazon S3 Client created!!!");
        log.setTimestamp(System.currentTimeMillis());
        
		ObjectMetadata md = new ObjectMetadata();
		int f=wreck.toString().getBytes().length;
        md.setContentLength(f);
        md.setContentType("application/json");
       
        ByteArrayInputStream greetingIS = new ByteArrayInputStream(wreck.toString().getBytes());
        
        String key="Object" + k;
        k++;
        
		PutObjectRequest putRequest1=new PutObjectRequest("shipwreck-bucket", key,greetingIS,md );
		PutObjectResult putResult1=s3Client.putObject(putRequest1);
		InputLogEvent log1 = new InputLogEvent();
        log1.setMessage("S3 Client request sent to aws.amazon.com!!");
        log1.setTimestamp(System.currentTimeMillis());
		// Code for logging 
		
		String token=null;
			
		AWSLogsClient cloudWatchLogs=new AWSLogsClient();
		cloudWatchLogs.setRegion(Region.getRegion(Regions.US_EAST_1));
		
		
		PutLogEventsRequest logRequest=new PutLogEventsRequest();
		logRequest.setLogGroupName("Shipwreck_LogGroup");
		logRequest.setLogStreamName("Shipwreck_LogStream");
		
		DescribeLogStreamsRequest logStreamsrequest = new DescribeLogStreamsRequest().withLogGroupName("Shipwreck_LogGroup").withLogStreamNamePrefix("Shipwreck");
		List<LogStream> logStreamList = new ArrayList<>();
		logStreamList= cloudWatchLogs.describeLogStreams(logStreamsrequest).getLogStreams();
		
		for(LogStream logStream: logStreamList) {
			token=logStream.getUploadSequenceToken();
		}
		if(token!=null)
		{
			logRequest.setSequenceToken(token);
		}
		
		
		InputLogEvent log2 = new InputLogEvent();
        log2.setMessage("Object uploaded!!");
        log2.setTimestamp(System.currentTimeMillis());
        
        ArrayList<InputLogEvent> logEvents = new ArrayList<InputLogEvent>();
        logEvents.add(log);
        logEvents.add(log1);
        logEvents.add(log2);
        
        logRequest.setSequenceToken(logRequest.getSequenceToken());
        logRequest.setLogEvents(logEvents);
        
        cloudWatchLogs.putLogEvents(logRequest);
		
		System.out.println("Object Uploaded");
		
		return shipwreckRepositoty.saveAndFlush(wreck);
	}
	
}
