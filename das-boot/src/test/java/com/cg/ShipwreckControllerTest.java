package com.cg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cg.controller.ShipwreckController;
import com.cg.model.Shipwreck;
import com.cg.repository.ShipwreckRepository;

public class ShipwreckControllerTest {

	@InjectMocks
	private ShipwreckController sc;
	
	@Mock
	private ShipwreckRepository shipwreckRepository;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testShipwreckGet() {
		Shipwreck sw=new Shipwreck();
		sw.setId(1L);
		when(shipwreckRepository.getOne(1L)).thenReturn(sw);
		
		Shipwreck wreck=sc.get(1L);
		assertEquals(1L, wreck.getId().longValue());
	}
	
	@Test
	public void testShipwreckList() {
		List<Shipwreck> wrecks=new ArrayList<>();
		Shipwreck sw=new Shipwreck();
		sw.setId(1L);
		sw.setName("Test1");
		wrecks.add(sw);
		Shipwreck sw1=new Shipwreck();
		sw1.setId(2L);
		sw1.setName("Test2");
		wrecks.add(sw1);
		when(shipwreckRepository.findAll()).thenReturn(wrecks);
		
		List<Shipwreck> resultWrecks=new ArrayList<>();
		resultWrecks=sc.list();
		assertEquals(wrecks.size(), resultWrecks.size());
	}
	
	@Test
	public void testShipwreckUpdate() {
		Shipwreck a = new Shipwreck(1L, "U869", "A very deep German UBoat", "FAIR", 200, 44.12, 138.44, 1994);
		when(shipwreckRepository.getOne(1L)).thenReturn(a);
		
		Shipwreck wreck = new Shipwreck(1L, "U869", "A very deep German UBoat", "FAIR", 400, 44.12, 138.44, 1994);
		when(shipwreckRepository.saveAndFlush(wreck)).thenReturn(wreck);
		
		Shipwreck resultWreck=new Shipwreck();
		resultWreck=sc.update(1L, wreck);
		System.out.println(resultWreck);
		assertEquals(wreck.getDepth(), resultWreck.getDepth());
	}
	
	@Test
	public void testShipwreckCreate() {
		Shipwreck a = new Shipwreck(1L, "U869", "A very deep German UBoat", "FAIR", 200, 44.12, 138.44, 1994);
		when(shipwreckRepository.saveAndFlush(a)).thenReturn(a);
		
		Shipwreck resultWreck=new Shipwreck();
		resultWreck=sc.post(a);
		assertNotNull(resultWreck);
	}
	
	@Test
	public void testShipwreckDelete() {
		Shipwreck a = new Shipwreck(1L, "U869", "A very deep German UBoat", "FAIR", 200, 44.12, 138.44, 1994);
		when(shipwreckRepository.getOne(1L)).thenReturn(a);
		
		Shipwreck resultWreck=new Shipwreck();
		resultWreck=sc.shipwreckDelete(1L);
		verify(shipwreckRepository, times(1)).deleteById(1L);
		
		assertEquals(1L, resultWreck.getId().longValue());
	}
}
