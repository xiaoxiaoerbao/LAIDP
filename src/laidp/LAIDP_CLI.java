package laidp;

import utils.Benchmark;
import org.apache.commons.cli.*;
import java.util.BitSet;

public class LAIDP_CLI {


    /**
     * Required parameters
     */
    String genotypeFile;
    String taxaGroupFile;
    String ancestralAllele;
    String localAnceOutFile;


    /**
     * Optional parameters
     */
    int windowSize;
    int stepSize;
    int conjunctionNum;
    double switchCostScore;

    int maxSolutionCount;

    int threadsNum;

    boolean ifTract=false;

    public LAIDP_CLI(org.apache.commons.cli.CommandLine line){
        this.genotypeFile=line.getOptionValue("g");
        if (line.hasOption("w")){
            this.windowSize=Integer.parseInt(line.getOptionValue("w"));
        }else {
            this.windowSize=200;
        }
        if (line.hasOption("s")){
            this.stepSize = Integer.parseInt(line.getOptionValue("s"));
        }else {
            this.stepSize = 100;
        }
        this.taxaGroupFile = line.getOptionValue("taxaGroup");
        this.ancestralAllele = line.getOptionValue("ancestral");
        if (line.hasOption("conjunctionNum")){
            this.conjunctionNum = Integer.parseInt(line.getOptionValue("conjunctionNum"));
        }else {
            this.conjunctionNum= 2;
        }
        if (line.hasOption("switchCost")){
            this.switchCostScore=Double.parseDouble(line.getOptionValue("switchCost"));
        }else {
            this.switchCostScore = 1.5;
        }
        if (line.hasOption("maxSolution")){
            this.maxSolutionCount = Integer.parseInt(line.getOptionValue("maxSolution"));
        }else {
            this.maxSolutionCount = 32;
        }
        this.localAnceOutFile = line.getOptionValue("out");
        if (line.hasOption("t")){
            this.threadsNum = Integer.parseInt(line.getOptionValue("t"));
        }else {
            this.threadsNum = 1;
        }
        if (line.hasOption("tract")){
            this.ifTract = true;
        }
    }

    public void startRun(){
        GenotypeTable genotypeTable = new GenotypeTable(genotypeFile);
        BitSet[] ancestralAlleleBitSet = genotypeTable.getAncestralAlleleBitSet(ancestralAllele);
        BitSet[][] localAnc = genotypeTable.calculateLocalAncestry(windowSize, stepSize, taxaGroupFile,
                ancestralAlleleBitSet, conjunctionNum, switchCostScore, maxSolutionCount, threadsNum);
        if (ifTract){
            genotypeTable.write_tract(localAnc, taxaGroupFile, localAnceOutFile);
        }else {
            genotypeTable.write_localAncestry(localAnc, localAnceOutFile, taxaGroupFile);
        }
    }

    private static Options SetOptions(){
        Option genotypeFile = new Option("g", "genotypeFile",true,"<fileName>, path of genotype file in vcf format");
        Option windowSize = new Option("w", "windowSize", true, "<integer> window size in variants");
        Option stepSize = new Option("s", "stepSize", true, "<integer> step size in variants");
        Option taxaGroupFile = new Option("taxaGroup","taxaGroupFile", true, "<fileName>, path of taxaGroup" +
                " file");
        Option ancestalyAllele = new Option("ancestral", "ancestralAllele", true, "<integer|fileName> ancestral " +
                "allele, " +
                "integer represent outGroup sample index, separated by comma when multiple outGoups exists; when it " +
                "is string, ancestral allele file is required");
        Option conjunctionNum = new Option("conjunctionNum", "conjunctionNum", true, "the number of grid when define local " +
                "introgressed interval");
        Option switchCostScore = new Option("switchCost", "switchCostScore",true, "switch cost score when haplotype" +
                " " +
                "switching occurs");
        Option maxSolutionCount = new Option("maxSolution","maxSolutionCount",true, "upper bound of the candidate solution");
        Option localAncestyOutFile = new Option("out", "outFile",true, "prefix of outfile");
        Option threadsNum = new Option("t", "threadsNum",true, "thread number");
        Option tract = new Option("tract","introgressed tract", false, "output in the form of tract");
        Options options = new Options();
        options.addOption(genotypeFile);
        options.addOption(windowSize);
        options.addOption(stepSize);
        options.addOption(taxaGroupFile);
        options.addOption(ancestalyAllele);
        options.addOption(conjunctionNum);
        options.addOption(switchCostScore);
        options.addOption(maxSolutionCount);
        options.addOption(localAncestyOutFile);
        options.addOption(threadsNum);
        options.addOption(tract);
        return options;
    }

    private static String getCmdLineSyntax(){
        return "java -jar LAIDP.jar -g genotypeFile" + " " +
                "-taxaGroup taxaGroupFile" + " " +
                "-ancestral ancestral" + " " +
                "-out outFile";
    }

    private static String getHeader(){
        return "LAIDP v1.0";
    }

    private static String getFooter(){
        return "Author: Daxing Xu, email: dxxu@genetics.ac.cn\nSee below for detailed documentation\nhttps://github.com/xiaoxiaoerbao/LAIDP";
    }

    public static void startFromCLI(String[] args){
        Options options = LAIDP_CLI.SetOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        HelpFormatter helpFormatter = new HelpFormatter();
        if (args.length < 1) {
            helpFormatter.printHelp(getCmdLineSyntax(), getHeader(), options,getFooter());
            System.exit(1);
        }
        long start = System.nanoTime();
        try {
            line=parser.parse(options, args);
            new LAIDP_CLI(line).startRun();
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            helpFormatter.printHelp(getCmdLineSyntax(), getHeader(), options, getFooter());
            System.exit(1);
        }
        System.out.println("Completed in "+ Benchmark.getTimeSpanMinutes(start)+" minutes");
        System.out.println("Done");
    }

    public static void main(String[] args) {
//        String genotypeFile = "/Users/xudaxing/Desktop/LAIDP_development/two_way_ancient_test2/003_simulation/D001.vcf";
//        int windowSize = 200;
//        int stepSize = 100;
//        String taxaGroupFile = "/Users/xudaxing/Desktop/LAIDP_development/two_way_ancient_test2/004_runner/laidp/D001.taxaGroup.txt";
//        String ancestryAllele = "simulation";
//        int conjunctionNum = 2;
//        double switchCostScore = 1.5;
//        int maxSolutionCount = 32;
//        String localAnceOutFile = "/Users/xudaxing/Desktop/LAIDP_development/two_way_ancient_test2/004_runner/laidp/D001.localAnc2.txt";
//        int threadsNum = 2;
////
//
//        String genotypeFile = args[0];
//        int windowSize = Integer.parseInt(args[1]);
//        int stepSize = Integer.parseInt(args[2]);
//        String taxaGroupFile = args[3];
//        String ancestryAllele = args[4];
//        int conjunctionNum = Integer.parseInt(args[5]);
//        double switchCostScore = Double.parseDouble(args[6]);
//        int maxSolutionCount = Integer.parseInt(args[7]);
//        String localAnceOutFile = args[8];
//        int threadsNum = Integer.parseInt(args[9]);
//
//
////////
//        GenotypeTable.run_LAIDP(genotypeFile, windowSize, stepSize, taxaGroupFile, ancestryAllele, conjunctionNum,
//                switchCostScore, maxSolutionCount, localAnceOutFile, threadsNum);

        LAIDP_CLI.startFromCLI(args);
    }






}
