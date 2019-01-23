package com.cg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cg.model.Shipwreck;
import com.cg.repository.ShipwreckRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=App.class)
public class ShipwreckRepositoryIntegrationTesting {

	@Autowired
	private ShipwreckRepository shipwreckRepository;
	
	@Test
	public void testFindAll() {
		List<Shipwreck> wrecks=shipwreckRepository.findAll();
		assertThat(wrecks.size(),is(greaterThanOrEqualTo(0)));
	}
	
	@Test
	public void testFindOne() {
		Shipwreck wreck=shipwreckRepository.getOne(65L);
		assertThat(wreck.getId().longValue(), is(65L));
	}
	
	@Test
	public void testCreate() {
		Shipwreck a = new Shipwreck(1L, "U869", "A very deep German UBoat", "FAIR", 200, 44.12, 138.44, 1994);
		Shipwreck result=shipwreckRepository.saveAndFlush(a);
		
		assertNotNull(result);
	}
	
	@Test
	public void testDelete() {
		
		shipwreckRepository.deleteById(1L);
		Shipwreck result=shipwreckRepository.getOne(1L);
		System.out.println(result);
		verify(shipwreckRepository,times(1)).deleteById(1L);
		assertNull(result);
	}
}
