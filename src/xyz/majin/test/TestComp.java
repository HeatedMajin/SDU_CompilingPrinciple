package xyz.majin.test;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import xyz.majin.utils.Utils;
import xyz.majin.utils.WordBean;
import xyz.majn.comp.MyCompiler;

public class TestComp {
	//@Test
	public void testGetChar() throws IOException {
		try {
			char c;
			while (true) {
				c = Utils.getChar();
				System.out.println(c);
			}
		} catch (EOFException e) {
			System.out.println("����");
		}
	}

//	@Test
	public void testSym() {
		MyCompiler.getSym(true);
	}

	@Test
	public void testBlock(){
		MyCompiler.getSym(false);
		MyCompiler.Block(true);
		System.out.println();
	}
}
