package genepi.haplogrep.main;

import genepi.base.Tool;
import genepi.haplogrep.Session;
import genepi.haplogrep.util.HgClassifier;
import genepi.haplogrep.util.ExportTools;
import importer.HsdImporter;
import importer.VcfImporter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import phylotree.Annotation;
import core.SampleFile;

public class Haplogrep extends Tool {

	public static String VERSION = "v2.1.13";

	public Haplogrep(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {

		addParameter("in", "input VCF or hsd file");
		addParameter("out", "haplogroup output file");
		addParameter("format", "vcf or hsd");
		addOptionalParameter("phylotree", "specifiy phylotree version", Tool.STRING);
		addFlag("rsrs", "use RSRS Version");
		addFlag("extend-report", "add flag for a extended final output");
		addFlag("chip", "VCF data from a genotype chip");
		addOptionalParameter("metric", "specifiy other metric (hamming or jaccard)", Tool.STRING);
		addFlag("lineage", "export lineage information");
	}

	@Override
	public void init() {

		System.out.println("Welcome to HaploGrep " + VERSION);
		System.out.println("(c) Division of Genetic Epidemiology, Medical University of Innsbruck");
		System.out.println("Hansi Weissensteiner, Lukas Forer, Dominic Pacher and Sebastian Schönherr");
		System.out.println("");

	}

	@Override
	public int run() {

		String in = (String) getValue("in");
		String out = (String) getValue("out");
		String tree = (String) getValue("phylotree");
		String format = (String) getValue("format");
		String metric = (String) getValue("metric");

		boolean extended = isFlagSet("extend-report");

		boolean chip = isFlagSet("chip");

		boolean lineage = isFlagSet("lineage");

		boolean rsrs = isFlagSet("rsrs");

		if (chip && !format.equals("vcf")) {
			System.out.println(
					"The --chip flag only works for VCF. For hsd, please specify the included variants in the Haplogrep range.");
			return -1;
		}

		if (metric == null) {
			metric = "kulczynski";
		}

		if (tree == null) {
			tree = "17";
		}

		File input = new File(in);

		if (!input.exists()) {
			System.out.println("Error. Please check if input file exists");
			return -1;
		}

		String phylotree = "phylotree/phylotree$VERSION$RSRS.xml";

		String fluctrates = "weights/weights$VERSION$RSRS.txt";

		phylotree = phylotree.replace("$VERSION", tree);

		fluctrates = fluctrates.replace("$VERSION", tree);

		if (rsrs) {
			phylotree = phylotree.replace("$RSRS", "_rsrs");
			fluctrates = fluctrates.replace("$RSRS", "_rsrs");
		} else {
			phylotree = phylotree.replace("$RSRS", "");
			fluctrates = fluctrates.replace("$RSRS", "");
		}

		System.out.println(phylotree);
		System.out.println("Parameters:");
		System.out.println("Input Format: " + format);
		System.out.println("Phylotree Version: " + tree);
		System.out.println("Reference: " + (rsrs ? "RSRS" : "rCRS"));
		System.out.println("Extended Report: " + extended);
		System.out.println("Used Metric: " + metric);
		System.out.println("Chip array data: " + chip);
		System.out.println("Lineage: " + lineage);
		System.out.println("");

		Annotation.setAnnotationPath("annotation/aminoacidchange.txt");
		
		long start = System.currentTimeMillis();

		System.out.println("Start Classification...");

		try {

			if (input.isFile()) {

				String uniqueID = UUID.randomUUID().toString();

				Session session = new Session(uniqueID);

				ArrayList<String> lines = null;

				if (format.equals("hsd")) {

					HsdImporter importer = new HsdImporter();
					lines = importer.loadHsd(input);

				}

				else if (format.equals("vcf")) {

					VcfImporter importer = new VcfImporter();
					lines = importer.vcfToHsd(input, chip);

				}

				if (lines != null) {

					SampleFile newSampleFile = new SampleFile(lines);

					session.setCurrentSampleFile(newSampleFile);

					HgClassifier.run(session, phylotree, fluctrates, metric);

					ExportTools.createReport(session, out, extended);

					if (lineage) {
						ExportTools.calcLineage(session, out);
					}
				}

			} else {
				return -1;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

		System.out.println("HaploGrep file written to " + out + " (Time: "
				+ ((System.currentTimeMillis() - start) / 1000) + " sec)");

		return 0;
	}

	public static void main(String[] args) throws IOException {

		Haplogrep haplogrep = new Haplogrep(args);

		 //haplogrep = new Haplogrep(new String[] { "--in",
		 //"test-data/vcf/ALL.chrMT.phase1.vcf", "--out",
		// "test-data/h100-haplogrep.txt", "--format", "vcf","--extend-report"});

		haplogrep.start();

	}

}
