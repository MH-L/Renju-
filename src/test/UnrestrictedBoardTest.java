package test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import model.UnrestrictedBoard;

public class UnrestrictedBoardTest {
	private UnrestrictedBoard bd;
	
	@Before
	public void initialize() {
		bd = new UnrestrictedBoard();
	}
	
	@Test
	public void testUpdateAndCheckWinning() {
		assertTrue(bd.updateBoard(0, true));
		assertTrue(bd.updateBoard(1, false));
		assertFalse(bd.updateBoard(1, true));
		bd.updateBoard(16, true);
		bd.updateBoard(32, true);
		bd.updateBoard(48, true);
		bd.updateBoard(64, true);
		assertTrue(bd.someoneWins());
		bd.render();
		bd.reset();
		bd.updateBoard(4, true);
		bd.updateBoard(18, true);
		bd.updateBoard(32, true);
		bd.updateBoard(46, true);
		bd.updateBoard(60, true);
		bd.render();
		assertTrue(bd.someoneWins());
	}
	
	@Test
	public void testEvaluateLine() {
		
	}
	
	@Test
	public void testEvaluateBoard() {
		bd.updateBoard(4, true);
		bd.updateBoard(18, true);
		bd.updateBoard(32, true);
		bd.updateBoard(46, true);
		bd.updateBoard(60, true);
		assertEquals(1000000, bd.getHeuristics());
		bd.render();
		bd.reset();
		bd.updateBoard(4, true);
		bd.updateBoard(18, true);
		bd.updateBoard(32, true);
		assertEquals(4, bd.getHeuristics());
		bd.updateBoard(46, true);
		assertEquals(15, bd.getHeuristics());
		bd.reset();
		bd.render();
		bd.updateBoard(4, true);
		bd.updateBoard(18, true);
		bd.updateBoard(32, true);
		bd.updateBoard(60, true);
		assertEquals(13, bd.getHeuristics());
		bd.updateBoard(46, false);
		assertEquals(0, bd.getHeuristics());
		bd.updateBoard(33, true);
		assertEquals(5, bd.getHeuristics());
		bd.render();
		
		bd.reset();
		bd.updateBoard(2, true);
		bd.updateBoard(18, true);
		bd.updateBoard(34, true);
		bd.updateBoard(50, true);
		assertEquals(bd.getHeuristics(), 15);
		bd.updateBoard(66, true);
		assertEquals(bd.getHeuristics(), 1000000);
	}
	
	@Test
	public void testNextMoves() {
		bd.updateBoard(0, true);
		bd.updateBoard(16, true);
		bd.updateBoard(208, true);
		bd.render();
		Set<Integer> nextMoves = bd.nextMoves();
		assertFalse(nextMoves.contains(0));
	}
	
	@Test
	public void withdrawalTest() {
		bd.updateBoard(16, true);
		bd.updateBoard(17, false);
		bd.updateBoard(27, true);
		bd.withdrawMove(16);
		bd.render();
	}
	
	@Test
	public void testFormedThreat() {
		int blocking[] = new int[]{0};
		bd.updateBoard(12, true);
		bd.updateBoard(26, true);
		assertFalse(bd.formedThreat(true, 26, blocking));
		bd.updateBoard(40, true);
		bd.updateBoard(54, true);
		assertTrue(bd.formedThreat(true, 54, blocking));
		assertEquals(blocking[0], 68);
		bd.render();
		bd.reset();
		bd.updateBoard(112, true);
		bd.updateBoard(114, false);
		bd.updateBoard(128, true);
		bd.updateBoard(144, false);
		bd.updateBoard(96, true);
		bd.updateBoard(99, false);
		bd.updateBoard(64, true);
		assertTrue(bd.formedThreat(true, 128, new int[]{0}));
		bd.render();
	}

	@Test
    public void testGetInc() {
	    bd.updateBoard(112, true);
//	    bd.updateBoard(128, false);
//	    bd.updateBoard(114, true);
//	    bd.updateBoard(113, false);
//	    bd.updateBoard(98, true);
//	    bd.updateBoard(82, false);
//	    bd.updateBoard(126, true);
	    bd.render();
	    System.out.println(bd.getInc(128, false));
    }
}
