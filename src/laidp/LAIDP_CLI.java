package laidp;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import utils.*;
import org.apache.commons.cli.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;

public class LAIDP_CLI {


    /**
     * Required parameters
     */
    String genotypeFile;
    String taxaGroupFile;
    String ancestralAllele;
    String localAnceOutFilePrefix;


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
    boolean ancestryProportion = false;

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
        this.localAnceOutFilePrefix = line.getOptionValue("out");
        if (line.hasOption("t")){
            this.threadsNum = Integer.parseInt(line.getOptionValue("t"));
        }else {
            this.threadsNum = 1;
        }
        if (line.hasOption("tract")){
            this.ifTract = true;
        }
        if (line.hasOption("ancestryProportion")){
            this.ancestryProportion=true;
        }
    }

    public void startRun(){
        GenotypeTable genotypeTable = new GenotypeTable(genotypeFile);
        BitSet[] ancestralAlleleBitSet = genotypeTable.getAncestralAlleleBitSet(ancestralAllele);
        BitSet[][] localAnc = genotypeTable.calculateLocalAncestry(windowSize, stepSize, taxaGroupFile,
                ancestralAlleleBitSet, conjunctionNum, switchCostScore, maxSolutionCount, threadsNum);
        genotypeTable.write_localAncestry(localAnc, localAnceOutFilePrefix+".lai", taxaGroupFile);
        if (ifTract){
            genotypeTable.write_tract(localAnc, taxaGroupFile, localAnceOutFilePrefix+".tract");
        }if (ancestryProportion){
            genotypeTable.write_localAncestryProportion(localAnc, localAnceOutFilePrefix+".proportion");
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
        Option localAncestryOutFilePrefix = new Option("out", "prefix of outFile",true, "prefix of outfile");
        Option threadsNum = new Option("t", "threadsNum",true, "thread number");
        Option tract = new Option("tract","introgressed tract", false, "output in the form of tract");
        Option ancestryProportion = new Option("ancestryProportion", "ancestry proportion", false, "ancestry " +
                "proportion per site");
        Options options = new Options();
        options.addOption(genotypeFile);
        options.addOption(windowSize);
        options.addOption(stepSize);
        options.addOption(taxaGroupFile);
        options.addOption(ancestalyAllele);
        options.addOption(conjunctionNum);
        options.addOption(switchCostScore);
        options.addOption(maxSolutionCount);
        options.addOption(localAncestryOutFilePrefix);
        options.addOption(threadsNum);
        options.addOption(tract);
        options.addOption(ancestryProportion);
        return options;
    }

    private static String getCmdLineSyntax(){
        return "java -jar LAIDP.jar -g genotypeFile" + " " +
                "-taxaGroup taxaGroupFile" + " " +
                "-ancestral ancestral" + " " +
                "-out prefix";
    }

    private static String getHeader(){
        return "LAIDP version: 0.1.1";
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
//        String genotypeFile = "/Users/xudaxing/Desktop/LAIDP_development/threeWay/003_simulation/D001.vcf";
//        int windowSize = 150;
//        int stepSize = 75;
//        String taxaGroupFile = "/Users/xudaxing/Desktop/LAIDP_development/threeWay/004_runner/laidp/D001.taxaGroup.txt";
//        String ancestryAllele = "simulation";
//        int conjunctionNum = 2;
//        double switchCostScore = 1.5;
//        int maxSolutionCount = 8;
//        String localAnceOutFile = "/Users/xudaxing/Desktop/LAIDP_development/threeWay/004_runner/laidp/D001.localAnc2" +
//                ".txt";
//        int threadsNum = 2;
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
////
////
////////////
//        GenotypeTable.run_LAIDP(genotypeFile, windowSize, stepSize, taxaGroupFile, ancestryAllele, conjunctionNum,
//                switchCostScore, maxSolutionCount, localAnceOutFile, threadsNum);

        LAIDP_CLI.startFromCLI(args);
//        splitChr(args[0],args[1], args[2], args[3]);
//        String inputFile = "/Users/xudaxing/Desktop/chr27.vcf.gz";
//        String outDir = "/Users/xudaxing/Desktop/temp";
//        String chr1 = "27";
//        String chr2 = "28";
//        splitChr(inputFile, outDir, chr1, chr2);
//        filter(args[0],args[1]);
//        String genotypeFile = args[0];
//        String outFile = args[1];
//        splitDiploid2Haploid(genotypeFile,outFile);


//        String ancestralAlleleFile = "/Users/xudaxing/Desktop/ancestralAllele_BGVD.txt.gz";
//        String dip2hapAlleleFile = "/Users/xudaxing/Desktop/dip2hap.allele.txt.gz";
//        String outFile = "/Users/xudaxing/Desktop/dip2hap.ancestralAllele.txt.gz";
////        retainAncAlleleFrom(args[0], args[1], args[2]);
//        long start = System.nanoTime();
//        retainAncAlleleFrom(ancestralAlleleFile, dip2hapAlleleFile, outFile);
//        System.out.println(Benchmark.getTimeSpanMilliseconds(start));

//        splitAncestralAllele(args[0], Integer.parseInt(args[1]), args[2]);
    }


    public static void splitChr(String inputFile, String outDir, String chr1, String chr2){
        try (BufferedReader br = IOTool.getReader(inputFile);
             BufferedWriter bw28 = IOTool.getWriter(new File(outDir, "chr"+chr1+".vcf.gz"));
             BufferedWriter bw29 =IOTool.getWriter(new File(outDir, "chr"+chr2+".vcf.gz"))) {
            String line;
            List<String> temp;
            while ((line=br.readLine()).startsWith("##")){
                bw28.write(line);
                bw29.write(line);
            }
            bw28.write(line);
            bw29.write(line);
            while ((line=br.readLine())!=null){
                temp = PStringUtils.fastSplit(line.substring(0,20));
                if (temp.get(0).equals(chr1)){
                    bw28.write(line);
                    bw28.newLine();
                }else if (temp.get(0).equals(chr2)){
                    bw29.write(line);
                    bw29.newLine();
                }
            }
            bw28.flush();
            bw29.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void filter(String inputFile, String outFile){
        try (BufferedReader br = IOTool.getReader(inputFile);
             BufferedWriter bw = IOTool.getWriter(outFile)) {
            String line;
            List<String> temp;
            while ((line=br.readLine()).startsWith("##")){
                bw.write(line);
                bw.newLine();
            }
            bw.write(line);
            bw.newLine();
            int variantsNum = 0, retainVariantsNum = 0;
            while ((line=br.readLine())!=null){
                variantsNum++;
                temp = PStringUtils.fastSplit(line);
                if (temp.subList(9,temp.size()).contains(".")) continue;
                retainVariantsNum++;
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
            System.out.println("variants num is "+variantsNum);
            System.out.println("retain variants num is "+retainVariantsNum);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void splitDiploid2Haploid(String genotypeFile, String outFile){
        try (BufferedReader br = IOTool.getReader(genotypeFile);
             BufferedWriter bw = IOTool.getWriter(outFile)) {
            String line;
            List<String> temp, tem;
            while ((line=br.readLine()).startsWith("##")){
                bw.write(line);
                bw.newLine();
            }
            temp = PStringUtils.fastSplit(line);
            StringBuilder sb = new StringBuilder();
            sb.setLength(0);
            sb.append(String.join("\t", temp.subList(0,9))).append("\t");
            for (int i = 9; i < temp.size(); i++) {
                tem = PStringUtils.fastSplit(temp.get(i));
                sb.append(tem.get(0)).append("_0").append("\t");
                sb.append(tem.get(0)).append("_1").append("\t");
            }
            sb.deleteCharAt(sb.length()-1);
            bw.write(sb.toString());
            bw.newLine();
            while ((line=br.readLine())!=null){
                temp = PStringUtils.fastSplit(line);
                if (temp.contains("./.")) continue;
                sb.setLength(0);
                sb.append(String.join("\t", temp.subList(0,9))).append("\t");
                for (int i = 9; i < temp.size(); i++) {
                    tem = PStringUtils.fastSplit(temp.get(i), "|");
                    sb.append(tem.get(0)).append("\t").append(tem.get(1)).append("\t");
                }
                sb.deleteCharAt(sb.length()-1);
                bw.write(sb.toString());
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void  retainAncAlleleFrom(String ancestralAlleleFile, String dip2hapAlleleFile, String outFile) {

        try (BufferedReader brAnc = IOTool.getReader(ancestralAlleleFile);
             BufferedReader brDip2Hap = IOTool.getReader(dip2hapAlleleFile);
             BufferedWriter bw = IOTool.getWriter(outFile)) {
            String line;
            List<String> temp;
            IntList[] chrPosList = new IntList[29];
            for (int i = 0; i < 29; i++) {
                chrPosList[i] = new IntArrayList();
            }
            int chr, pos;
            while ((line=brDip2Hap.readLine())!=null){
                temp = PStringUtils.fastSplit(line);
                chr = Integer.parseInt(temp.get(0));
                pos = Integer.parseInt(temp.get(1));
                chrPosList[chr-1].add(pos);
            }
            while ((line=brAnc.readLine())!=null){
                temp = PStringUtils.fastSplit(line);
                chr = Integer.parseInt(temp.get(0));
                pos = Integer.parseInt(temp.get(1));
                if (chrPosList[chr-1].contains(pos)){
                    bw.write(line);
                    bw.newLine();
                }
            }
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void splitAncestralAllele(String ancestralAlleleFile, int chr, String outFile){
        try (BufferedReader br = IOTool.getReader(ancestralAlleleFile);
             BufferedWriter bw = IOTool.getWriter(outFile)) {
            String line;
            List<String> temp;
            StringBuilder sb = new StringBuilder();
            while ((line=br.readLine())!=null){
                temp = PStringUtils.fastSplit(line);
                if (chr==Integer.parseInt(temp.get(0))){
                    sb.setLength(0);
                    sb.append(String.join("\t", temp.subList(1, temp.size())));
                    bw.write(sb.toString());
                    bw.newLine();
                }
            }
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
