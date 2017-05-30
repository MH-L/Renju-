package test;

import org.junit.*;

import algorithm.BoardTree;
import model.RestrictedBoard;
import model.UnrestrictedBoard;

public class BoardTreeTest {
	UnrestrictedBoard bd;
	RestrictedBoard bdr;
	
	@Before
	public void initialize() {
		bd = new UnrestrictedBoard();
		bdr = new RestrictedBoard();
	}
	
	@Test
	public void testAlphaBeta() {
		bd.updateBoard(96, true);
		bd.updateBoard(97, false);
		bd.updateBoard(111, false);
		bd.updateBoard(112, true);
		bd.updateBoard(113, false);
		bd.updateBoard(114, true);
		bd.updateBoard(127, true);
		bd.updateBoard(128, true);
		bd.updateBoard(143, false);
		bd.updateBoard(144, false);
		bd.updateBoard(145, false);
		bd.withdrawMove(145);
		bd.render();
		System.out.println(bd.evaluateBoard());
		int move = BoardTree.alphaBeta(bd, 6, Integer.MIN_VALUE, 
				Integer.MAX_VALUE, true, new int[]{0});
		System.out.println(move);
		System.out.println(BoardTree.nodesNum);
	}
}
