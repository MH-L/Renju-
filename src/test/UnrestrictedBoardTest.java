package test;

import static org.junit.Assert.*;

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
		assertEquals(1000000, bd.evaluateBoard(true));
		bd.render();
		bd.reset();
		bd.updateBoard(4, true);
		bd.updateBoard(18, true);
		bd.updateBoard(32, true);
		assertEquals(4, bd.evaluateBoard(true));
		bd.updateBoard(46, true);
		assertEquals(15, bd.evaluateBoard(true));
		bd.reset();
		bd.render();
		bd.updateBoard(4, true);
		bd.updateBoard(18, true);
		bd.updateBoard(32, true);
		bd.updateBoard(60, true);
		assertEquals(20, bd.evaluateBoard(true));
		bd.updateBoard(46, false);
		assertEquals(0, bd.evaluateBoard(true));
		bd.updateBoard(33, true);
		assertEquals(5, bd.evaluateBoard(true));
		bd.render();
	}
	
	@Test
	public void testNextMoves() {
		
	}
}
