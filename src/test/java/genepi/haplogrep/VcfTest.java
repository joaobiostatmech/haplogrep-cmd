package genepi.haplogrep;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import genepi.haplogrep.vcf.VcfImporter;

public class VcfTest {

	@Test
	public void IndelTest() throws Exception {
		String file = "test-data/vcf/testIndel.vcf";
		StringBuilder actual = new StringBuilder();
		VcfImporter impvcf = new VcfImporter();
		ArrayList<String> samples = impvcf.vcfToHsd(new File(file), false);

		// System.out.println(samples.toString());
		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals("8281-8289d,8307-8307d,8860G,", actual.toString());

	}

	@Test
	public void test1000Del() throws Exception {
		String file = "test-data/vcf/1000Gdel.vcf";
		StringBuilder actual = new StringBuilder();
		VcfImporter impvcf = new VcfImporter();
		ArrayList<String> samples = impvcf.vcfToHsd(new File(file), false);
		
		for(int i = 0; i < samples.size(); i++) {
			System.out.println(samples.get(i));
		}

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals("249-249d,290-291d,308-308d,310-310d,498-498d,521-524d,523-524d,8281-8289d,16188T,8307-8307d,",
				actual.toString());

		splits = samples.get(1).split("\t");
		actual.setLength(0);
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}
		
		//TODO ,309.1CC,
		assertEquals("105T,249-249d,", actual.toString());

		splits = samples.get(2).split("\t");
		actual.setLength(0);
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}
		assertEquals("309T,", actual.toString());

		splits = samples.get(3).split("\t");
		actual.setLength(0);
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}
		//TODO 309.1C,
		assertEquals("16189-16189d,", actual.toString());

	}

}