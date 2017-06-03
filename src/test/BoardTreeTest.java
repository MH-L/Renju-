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
		xcoord = Arrays.asList(8,4,5,3,4);
		ycoord = Arrays.asList(5,7,7,10,5);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.render();
		System.out.println(BoardTree.alphaBeta(bd, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				false, new int[]{0}));
		
		bd.reset();
		
		xcoord = Arrays.asList(9,10,11,12,13);
		ycoord = Arrays.asList(7,6,6,6,6);
		updateBoardInBatch(bd, ycoord, xcoord, true);
		xcoord = Arrays.asList(9,10,11,12);
		ycoord = Arrays.asList(6,7,7,7);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.render();
		System.out.println(BoardTree.alphaBeta(bd, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				false, new int[]{0}));
		
		bd.reset();
		xcoord = Arrays.asList(6,5,6,4,6,7,8,9,6,9,8);
		ycoord = Arrays.asList(3,4,4,5,5,5,5,5,6,6,7);
		updateBoardInBatch(bd, ycoord, xcoord, true);
		xcoord = Arrays.asList(6,7,8,5,10,11,3,5,7,6,7);
		ycoord = Arrays.asList(2,4,4,5,5,5,6,6,6,7,8);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.render();
		System.out.println(BoardTree.alphaBeta(bd, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				true, new int[]{0}));
		
		System.out.println(BoardTree.nodesNum);
	}
	
	@Test
	public void testConfused() {
		List<Integer> xcoord = Arrays.asList(5,7,8,5,6,5,7,6,8,10);
		List<Integer> ycoord = Arrays.asList(7,7,7,8,8,9,9,10,12,13);
		updateBoardInBatch(bd, ycoord, xcoord, true);
		xcoord = Arrays.asList(8,8,6,8,7,8,7,8,9);
		ycoord = Arrays.asList(6,8,9,9,10,10,11,11,12);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.render();
		System.out.println(BoardTree.alphaBeta(bd, 6, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				false, new int[]{0}));
	}
	
	private void updateBoardInBatch(AbstractBoard bd, List<Integer> ycoord, 
			List<Integer> xcoord, boolean first) {
		for (int i = 0; i < ycoord.size(); i++) {
			bd.updateBoard(ycoord.get(i)*AbstractBoard.width + xcoord.get(i), first);
		}
	}
}
