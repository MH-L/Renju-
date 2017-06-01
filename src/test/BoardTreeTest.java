package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.*;

import algorithm.BoardTree;
import model.AbstractBoard;
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
		int move = BoardTree.alphaBeta(bd, 4, Integer.MIN_VALUE, 
				Integer.MAX_VALUE, true, new int[]{0});
		System.out.println(move);
		System.out.println(BoardTree.nodesNum);
		
		bd.reset();
		bd.updateBoard(112, true);
		move = BoardTree.alphaBeta(bd, 7, Integer.MIN_VALUE, 
				Integer.MAX_VALUE, false, new int[]{0});
		System.out.println(move);
	}
	
	@Test
	public void additionalAlphaBetaTestRealExample() {
		List<Integer> ycoord = Arrays.asList(6,6,6,7,8,9);
		List<Integer> xcoord = Arrays.asList(5,6,7,6,5,4);
		updateBoardInBatch(bd, ycoord, xcoord, true);
		xcoord = Arrays.asList(4,8,4,5,3);
		ycoord = Arrays.asList(5,5,7,7,10);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.render();
		System.out.println(BoardTree.alphaBeta(bd, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				false, new int[]{0}));
	}
	
	private void updateBoardInBatch(AbstractBoard bd, List<Integer> ycoord, 
			List<Integer> xcoord, boolean first) {
		for (int i = 0; i < ycoord.size(); i++) {
			bd.updateBoard(ycoord.get(i)*AbstractBoard.width + xcoord.get(i), first);
		}
	}
}
