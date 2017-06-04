package test;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import algorithm.BoardTree;
import model.UnrestrictedBoard;

/**
 * Test class for threat-space search.
 * Although all relevant code are in AbstractBoard and BoardTree,
 * their tests are in separate files.
 * @author Kelvin Liu
 *
 */
public class TSSTest {
	private UnrestrictedBoard bd;
	
	@Before
	public void initialize() {
		bd = new UnrestrictedBoard();
	}
	
	@Test
	public void threatLocationTest() {
		// row based (straight line)
		bd.updateBoard(3, true);
		bd.updateBoard(4, true);
		bd.updateBoard(2, false);
		Map<Integer, Integer> thLocations = bd.findThreatLocation(true);
		assertTrue(thLocations.isEmpty());
		bd.updateBoard(6, true);
		thLocations = bd.findThreatLocation(true);
		assertTrue(thLocations.containsKey(5));
		assertTrue(thLocations.containsKey(7));
		assertEquals((int) thLocations.get(5), 7);
		assertEquals((int) thLocations.get(7), 5);
		bd.reset();
		bd.updateBoard(10, false);
		bd.updateBoard(11, true);
		bd.updateBoard(12, true);
		bd.updateBoard(14, true);
		assertTrue(bd.findThreatLocation(true).isEmpty());
		bd.reset();
		bd.updateBoard(9, false);
		bd.updateBoard(10, true);
		bd.updateBoard(11, true);
		bd.updateBoard(14, true);
		thLocations = bd.findThreatLocation(true);
		assertTrue(thLocations.containsKey(12));
		assertTrue(thLocations.containsKey(13));
		assertEquals((int) thLocations.get(12), 13);
		assertEquals((int) thLocations.get(13), 12);
		
		// l to r diagonal (skewed)
		bd.reset();
		bd.updateBoard(17, false);
		bd.updateBoard(33, true);
		bd.updateBoard(34, true);
		bd.updateBoard(49, true);
		bd.updateBoard(81, true);
		bd.render();
		thLocations = bd.findThreatLocation(true);
		assertTrue(thLocations.containsKey(65));
		assertTrue(thLocations.containsKey(97));
		assertEquals((int) thLocations.get(65), 97);
		assertEquals((int) thLocations.get(97), 65);
		
		// r to l diagonal (skewed)
		bd.reset();
		bd.updateBoard(27, false);
		bd.updateBoard(41, false);
		bd.updateBoard(69, false);
		bd.updateBoard(83, true);
		bd.updateBoard(97, false);
		bd.updateBoard(125, false);
		bd.render();
		thLocations = bd.findThreatLocation(false);
		assertTrue(thLocations.containsKey(13));
		assertTrue(thLocations.containsKey(55));
		assertEquals((int) thLocations.get(13), 55);
		assertEquals((int) thLocations.get(55), 13);
	}
	
	@Test
	public void threatSearcherTest() {
		bd.updateBoard(66, true);
		bd.updateBoard(111, false);
		bd.updateBoard(112, true);
		bd.updateBoard(125, false);
		bd.updateBoard(126, true);
		bd.updateBoard(127, true);
		bd.updateBoard(140, true);
		bd.updateBoard(141, false);
		bd.updateBoard(142, true);
		bd.updateBoard(146, false);
		bd.updateBoard(154, false);
		bd.updateBoard(157, false);
		bd.render();
		System.out.println(BoardTree.threatSpaceSearch(bd, 4, true));
	}
}
