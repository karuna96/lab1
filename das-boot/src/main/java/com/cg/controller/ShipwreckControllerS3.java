package com.cg.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.amazonaws.services.elasticloadbalancing.model.transform.AccessLogStaxUnmarshaller;
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

@RestController
public class ShipwreckControllerS3 {

	
	
	
	@PostMapping("post")
	public void storeOnject(@RequestBody Shipwreck ship) {
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTPS);
		clientConfig.setProxyHost("10.220.1.50");
		clientConfig.setProxyPort(8080);
		
		AmazonS3Client s3Client = new AmazonS3Client(clientConfig);
    	
		ObjectMetadata md = new ObjectMetadata();
		int f=ship.toString().getBytes().length;
        md.setContentLength(f);
        md.setContentType("application/json");
       
        ByteArrayInputStream greetingIS = new ByteArrayInputStream(ship.toString().getBytes());
        
		PutObjectRequest putRequest1=new PutObjectRequest("shipwreck-bucket", "Object",greetingIS,md );
		PutObjectResult putResult1=s3Client.putObject(putRequest1);
		System.out.println("Object Uploaded");
		
	}
	
	@PostMapping("log")
	public void CloudWatchLogs(@RequestBody String message) {
		
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTPS);
		clientConfig.setProxyHost("10.220.1.50");
		clientConfig.setProxyPort(8080);
		
		String token=null;
		
		
		AWSLogsClient cloudWatchLogs=new AWSLogsClient(clientConfig);
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
		
		
		InputLogEvent log = new InputLogEvent();
        log.setMessage(message);
        log.setTimestamp(System.currentTimeMillis());
        
        ArrayList<InputLogEvent> logEvents = new ArrayList<InputLogEvent>();
        logEvents.add(log);
        
        logRequest.setSequenceToken(logRequest.getSequenceToken());
        logRequest.setLogEvents(logEvents);
        
        cloudWatchLogs.putLogEvents(logRequest);
		
	}
	
	@PostMapping("cloudwatch")
	public void cloudWatch() {
		
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTPS);
		clientConfig.setProxyHost("10.220.1.50");
		clientConfig.setProxyPort(8080);
		
		AmazonCloudWatchClient cwc=new AmazonCloudWatchClient(clientConfig);
		
		GetMetricStatisticsRequest r=new GetMetricStatisticsRequest();
		r.setMetricName("AWS/S3");
		
		cwc.getMetricStatistics(r);
		
		System.out.println(cwc.getServiceName());
		
	}
}
