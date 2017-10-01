package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Main;
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
				Integer.MAX_VALUE, true);
		System.out.println(move);
		System.out.println(BoardTree.nodesNum);
		
		bd.reset();
		bd.updateBoard(112, true);
		move = BoardTree.alphaBeta(bd, 7, Integer.MIN_VALUE, 
				Integer.MAX_VALUE, false);
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
				false));
		
		bd.reset();
		
		xcoord = Arrays.asList(9,10,11,12,13);
		ycoord = Arrays.asList(7,6,6,6,6);
		updateBoardInBatch(bd, ycoord, xcoord, true);
		xcoord = Arrays.asList(9,10,11,12);
		ycoord = Arrays.asList(6,7,7,7);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.render();
		System.out.println(BoardTree.alphaBeta(bd, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				false));
		
		bd.reset();
		xcoord = Arrays.asList(6,5,6,4,6,7,8,9,6,9,8);
		ycoord = Arrays.asList(3,4,4,5,5,5,5,5,6,6,7);
		updateBoardInBatch(bd, ycoord, xcoord, true);
		xcoord = Arrays.asList(6,7,8,5,10,11,3,5,7,6,7);
		ycoord = Arrays.asList(2,4,4,5,5,5,6,6,6,7,8);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.render();
		System.out.println(BoardTree.alphaBeta(bd, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				true));
		
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
				false));
		
		bd.reset();
		xcoord = Arrays.asList(8,6,7,4,9,4,9,7,5,8,5,6,7,9);
		ycoord = Arrays.asList(1,2,2,3,3,4,4,5,6,6,7,7,7,8);
		updateBoardInBatch(bd, ycoord, xcoord, true);
		xcoord = Arrays.asList(8,6,7,8,5,6,7,8,4,6,6,7,8);
		ycoord = Arrays.asList(2,3,3,3,4,4,4,4,5,5,6,6,7);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.render();
		System.out.println(BoardTree.alphaBeta(bd, 6, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				false));
		
		bd.reset();
		xcoord = Arrays.asList(7,8,7,6,7,8);
		ycoord = Arrays.asList(5,6,7,8,9,9);
		updateBoardInBatch(bd, ycoord, xcoord, true);
		xcoord = Arrays.asList(6,9,6,7,6,5);
		ycoord = Arrays.asList(4,5,6,6,7,8);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.withdrawMove(125);
		bd.render();
		System.out.println(BoardTree.threatSpaceSearchV2(bd, 30, false, new int[]{0}, new int[]{0}));
		System.out.println(BoardTree.alphaBeta(bd, 8, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				false));
	}
	
	@Test
	public void testNonsense() {
		List<Integer> xcoord = Arrays.asList(5,8,10,8,12,6,9,11,5,8,13,5,7,8,10,13,5,10,13,7,12,6,11,14);
		List<Integer> ycoord = Arrays.asList(2,3,3,4,4,5,5,5,6,6,6,7,7,7,7,7,8,8,8,9,9,10,10,10);
		updateBoardInBatch(bd, ycoord, xcoord, true);
		xcoord = Arrays.asList(7,6,11,7,9,8,10,13,6,7,9,10,11,12,6,9,11,12,6,12,5,6,13);
		ycoord = Arrays.asList(2,3,3,4,4,5,5,5,6,6,6,6,6,6,7,7,7,7,8,8,9,9,9);
		updateBoardInBatch(bd, ycoord, xcoord, false);
		bd.render();
		System.out.println(BoardTree.alphaBeta(bd, 6, Integer.MIN_VALUE, Integer.MAX_VALUE, 
				false));
	}
	
	private void updateBoardInBatch(AbstractBoard bd, List<Integer> ycoord, 
			List<Integer> xcoord, boolean first) {
		for (int i = 0; i < ycoord.size(); i++) {
			bd.updateBoard(ycoord.get(i)*AbstractBoard.width + xcoord.get(i), first);
		}
	}

	private void updateBoardInBatchFromEmpty(AbstractBoard bd, int... moves) {
		if (bd.getStoneCount() != 0)
			return;
		boolean first = true;
		for (int move : moves) {
			bd.updateBoard(move, first);
			first = !first;
		}
	}
	
	private void updateBoardInBatchUsingRecord(AbstractBoard bd, String record) {
		String[] moves = record.split("\\|");
		int[] actual = new int[moves.length];
		for (int i = 0; i < moves.length; i++) {
			actual[i] = Integer.parseInt(moves[i].trim());
		}
		
		updateBoardInBatchFromEmpty(bd, actual);
	}

	@Test
	public void testAlphaBetaLearning() {
		bd.reset();
//		updateBoardInBatchFromEmpty(bd, 96,64,82,65,97,79,67,112,98,95,51,99,52,37,66);
//		updateBoardInBatchFromEmpty(bd, 96);
		updateBoardInBatchUsingRecord(bd, "111");
		Main.machineLearningSetup();
		bd.render();
		int[] value = new int[] {0};
        System.out.println(BoardTree.alphaBetaCustom(bd, 8, Integer.MIN_VALUE, Integer.MAX_VALUE, false, value,98, 3,3,0));
        System.out.println("Final output value: " + value[0]);
        value[0] = 0;
        System.out.println(BoardTree.alphaBetaMem(bd, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, false, value, 0));
        System.out.println("Final output value from mem: " + value[0]);
	}
}
