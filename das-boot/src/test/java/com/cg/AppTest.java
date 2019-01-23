package com.cg;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cg.controller.HomeController;

public class AppTest 
{
	@Test
    public void testApp()
    {
       HomeController hc=new HomeController();
       String result=hc.home();
       assertEquals(result, "Hello Capgeminite!");
    }
}
