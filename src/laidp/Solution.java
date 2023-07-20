package laidp;

import it.unimi.dsi.fastutil.ints.*;
import java.util.*;

public class Solution {

    /**
     * 需要性能优化
     * @param srcGenotype the first dim is haplotype, the second dim is SNP position
     * @param queryGenotype queryGenotype, 0 means reference allele, 1 means alternative allele
     * @param switchCostScore switchCostScore
     * @return mini cost score matrix, the first dim is haplotype, the second dim is SNP position
     */
    public static double[][] getMiniCostScore(BitSet[] srcGenotype, BitSet queryGenotype,
                                              double switchCostScore, int seqLen){
        //        double switchCostScore= 1.5;
//        int[][] srcGenotype = {{0,1,0,1,0,1,0,0,0,0,1,1},
//                            {0,0,0,1,0,1,1,0,0,0,1,1},
//                            {0,0,1,0,1,0,0,0,1,0,1,1},
//                            {0,0,0,0,1,0,1,0,1,1,1,1},
//                            {1,1,0,0,0,0,1,1,1,1,0,0},
//                            {1,0,0,1,0,0,1,1,1,1,0,0}};
//        int[] queryGenotype =       {1,1,0,0,0,1,0,0,1,1,1,1};

//        long start = System.nanoTime();
        int rowNum = srcGenotype.length;

        // distance
        double[][] distance = new double[rowNum][seqLen];
        int source, query;
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < seqLen; j++) {
                source = srcGenotype[i].get(j) ? 1 : 0;
                query = queryGenotype.get(j) ? 1 : 0;
                distance[i][j] = Math.abs(source-query);
            }
        }

        // initialize mini cost score
        double[][] miniCost = new double[rowNum][seqLen];
        for (int i = 0; i < miniCost.length; i++) {
            miniCost[i][0] = distance[i][0];
        }


        // i is SNP position
        // j is haplotype index of source population
        // miniCost
        for (int i = 1; i < seqLen; i++) {

            // j-1 SNP位置，单倍型路径发生switch对应的最小Cost
            double miniCostSwitch=Double.MAX_VALUE;
            for (int j = 0; j < distance.length; j++) {
                miniCostSwitch = Math.min(miniCost[j][i - 1], miniCostSwitch);
            }

//            for (int j = 0; j < rowNum; j++) {
//                // 最小cost路径对应当前haplotype
//                if (miniCost[j][i-1] < miniCostSwitch+switchCostScore){
//                    miniCost[j][i] = miniCost[j][i-1] + distance[j][i];
//                }else {
//                    // 最小cost路径对应转换单倍型
//                    miniCost[j][i] = miniCostSwitch+switchCostScore+distance[j][i];
//                }
//            }

            // Calculate miniCost matrix for current SNP position
            for (int j = 0; j < rowNum; j++) {
                miniCost[j][i] = miniCostSwitch + switchCostScore;
                if (miniCost[j][i - 1] < miniCostSwitch + switchCostScore) {
                    miniCost[j][i] = miniCost[j][i - 1];
                }
                miniCost[j][i] += distance[j][i];
            }
        }
//        System.out.println("calculate mini cost matrix take "+Benchmark.getTimeSpanSeconds(start)+ " seconds");
        return miniCost;
    }

    /**
     * 需要性能优化
     * @param srcGenotype the first dim is haplotype, the second dim is SNP position
     * @param queryGenotype queryGenotype, 0 means reference allele, 1 means alternative allele
     * @param switchCostScore switchCostScore
     * @param srcIndiList srcIndiList, order must same as srcGenotype
     * @param taxaSourceMap taxaSourceMap
     * @return candidate solution, it consists of multiple solutions with the same mini cost score
     * dim1 is mini cost score index, dim2 is solution
     * Solution consists of multiple groups, every three numbers as a group, representing a tract
     * the first number is source feature, it may contains multiple sources
     * the second and third number is start(inclusive) position and end(inclusive) position
     */
    public static IntList[] getCandidateSolution(BitSet[] srcGenotype, BitSet queryGenotype, double switchCostScore,
                                                 int seqLen, List<String> srcIndiList, Map<String, Source> taxaSourceMap) {

        double[][] miniCost = Solution.getMiniCostScore(srcGenotype, queryGenotype, switchCostScore, seqLen);
        int haplotypeLen = miniCost[0].length;

        // Find the index of the minimum cost score
        double miniCostScore = Double.MAX_VALUE;
        IntList miniCostScoreIndexList = new IntArrayList();
        for (int i = 0; i < miniCost.length; i++) {
            if (miniCost[i][haplotypeLen-1] < miniCostScore) {
                miniCostScore = miniCost[i][haplotypeLen-1];
                miniCostScoreIndexList.clear();
                miniCostScoreIndexList.add(i);
            } else if (miniCost[i][haplotypeLen-1] == miniCostScore) {
                miniCostScoreIndexList.add(i);
            }
        }

        // Initialize the candidate solutions
        IntList[] solutions = new IntList[miniCostScoreIndexList.size()];
        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = new IntArrayList();
            Source source = taxaSourceMap.get(srcIndiList.get(miniCostScoreIndexList.getInt(i)));
            solutions[i].add(Source.valueOf(source.name()).getFeature());
            solutions[i].add(haplotypeLen-1);
            solutions[i].add(haplotypeLen-1);
        }

        // Compute the candidate solutions
        for (int i = 0; i < solutions.length; i++) {
            IntSet currentIndexSet = new IntOpenHashSet();
            currentIndexSet.add(miniCostScoreIndexList.getInt(i));
            int currentSolutionElementIndex = 0;

            for (int j = haplotypeLen - 1; j > 0; j--) {
                IntSet nextIndexSet = new IntOpenHashSet();

                for (IntIterator it = currentIndexSet.iterator(); it.hasNext(); ) {
                    int index = it.nextInt();

                    // Add the current index to the next index set if the cost of staying at the current index is lower
                    if (miniCost[index][j-1] <= miniCost[index][j]) {
                        nextIndexSet.add(index);
                    }

                    // Add the indices of other individuals to the next index set if the cost of switching to them is lower
                    for (int k = 0; k < miniCost.length; k++) {
                        if (k != index && (miniCost[k][j-1] + switchCostScore) <= miniCost[index][j]) {
                            nextIndexSet.add(k);
                        }
                    }
                }

                int currentSourceFeature = Solution.getSourceFutureFrom(currentIndexSet, srcIndiList, taxaSourceMap);
                int nextSourceFeature = Solution.getSourceFutureFrom(nextIndexSet, srcIndiList, taxaSourceMap);
                if (currentSourceFeature == nextSourceFeature) {
                    solutions[i].set(currentSolutionElementIndex * 3 + 2, j-1);
                } else {
                    solutions[i].add(nextSourceFeature);
                    solutions[i].add(j-1);
                    solutions[i].add(j-1);
                    currentSolutionElementIndex++;
                }

                currentIndexSet = nextIndexSet;
            }
        }

        return solutions;
    }

    private static int getSourceFutureFrom(IntSet sourceIndexSet, List<String> srcIndiList,
                                           Map<String, Source> taxaSourceMap){
        EnumSet<Source> sourceEnumSet = EnumSet.noneOf(Source.class);
        for (int index : sourceIndexSet){
            sourceEnumSet.add(taxaSourceMap.get(srcIndiList.get(index)));
        }

        return Source.getSourceFeature(sourceEnumSet);
    }

    /**
     *
     * @param srcGenotypeFragment  the first dim is haplotype, the second dim is SNP position
     * @param queryGenotypeFragment queryGenotype, 0 means reference allele, 1 means alternative allele
     * @param fragmentLength fragmentLength
     * @param switchCostScore switchCostScore
     * @param srcIndiList srcIndiList, order must same as srcGenotype
     * @param taxaSourceMap taxaSourceMap
     * @param maxSolutionCount maxSolutionCount
     * @return solution, each element is a source feature (only contain one source), e.g., 1 or 2 or 4 or 8 or 16
     */
    public static int[] getSolution(BitSet[] srcGenotypeFragment,
                                    BitSet queryGenotypeFragment,
                                    int fragmentLength,
                                    double switchCostScore,
                                    List<String> srcIndiList,
                                    Map<String, Source> taxaSourceMap,
                                    int maxSolutionCount){
        IntList[] forwardCandidateSolutionCurrent = Solution.getCandidateSolution(srcGenotypeFragment,
                queryGenotypeFragment, switchCostScore, fragmentLength, srcIndiList, taxaSourceMap);
        IntList[] forwardCandidateSolutionNext = Solution.getCandidateSolution(srcGenotypeFragment,
                queryGenotypeFragment, switchCostScore+1, fragmentLength, srcIndiList, taxaSourceMap);

        int totalSolutionSizeCurrent = Solution.getMiniOptimalSolutionSize(forwardCandidateSolutionCurrent);
        int totalSolutionSizeNext = Solution.getMiniOptimalSolutionSize(forwardCandidateSolutionNext);

        //  totalSolutionSizeCurrent < 0 是因为 两个Int相乘的结果大于Int max
        if ((totalSolutionSizeCurrent > maxSolutionCount && totalSolutionSizeNext <= totalSolutionSizeCurrent/2) || totalSolutionSizeCurrent <= 0){
            return Solution.getSolution(srcGenotypeFragment, queryGenotypeFragment,
                    fragmentLength, switchCostScore+1, srcIndiList, taxaSourceMap, maxSolutionCount);
        }

        int[] forwardSolution = Solution.coalescentForward(forwardCandidateSolutionCurrent);
        if (forwardSolution.length==0) return forwardSolution;
        int seqLen = forwardSolution.length;
        IntList[] reverseCandidateSolution;
        int[] reverseSolution;
        IntList singleSourceFeatureList = Source.getSingleSourceFeatureList();
        singleSourceFeatureList.rem(1);
        if (singleSourceFeatureList.contains(forwardSolution[seqLen-1])){
            reverseCandidateSolution =
                    Solution.getCandidateSolution(Solution.reverseSrcGenotype(srcGenotypeFragment, fragmentLength),
                    Solution.reverseGenotype(queryGenotypeFragment,fragmentLength),switchCostScore, fragmentLength, srcIndiList,
                            taxaSourceMap);
            reverseSolution = Solution.coalescentReverse(reverseCandidateSolution);
            if (reverseSolution.length==0) return forwardSolution;
            for (int i = seqLen - 1; i > -1; i--) {
                if (reverseSolution[i]==1){
                    forwardSolution[i] = 1;
                }else {
                    break;
                }
            }
        }
        return forwardSolution;
    }

    /**
     * loter-like or major vote
     */
    public static int[] getLoterSolution(BitSet[] srcGenotypeFragment,
                                         BitSet queryGenotypeFragment,
                                         int fragmentLength,
                                         List<String> srcIndiList,
                                         Map<String, Source> taxaSourceMap){
        double[] switchCostScores = {1.5};
        IntList[] candidateSolutions;
        int[] solution;
        List<int[]> solutions = new ArrayList<>();
        int sourceFeature, start, end;
        for (double switchCostScore : switchCostScores) {
            candidateSolutions = Solution.getCandidateSolution(srcGenotypeFragment, queryGenotypeFragment,
                    switchCostScore, fragmentLength, srcIndiList, taxaSourceMap);
            for (IntList candidateSolution : candidateSolutions) {
                solution = new int[fragmentLength];
                for (int k = 0; k < candidateSolution.size(); k = k + 3) {
                    sourceFeature = candidateSolution.getInt(k);
                    start = candidateSolution.getInt(k + 2); // inclusive
                    end = candidateSolution.getInt(k + 1); // inclusice
                    Arrays.fill(solution, start, end + 1, sourceFeature);
                }
                solutions.add(solution);
            }
        }
        int[] finalSolution = new int[fragmentLength];
        Int2IntMap countMap;
        for (int posIndex = 0; posIndex < fragmentLength; posIndex++) {
            int maxCount = -1;
            int mode = -1;
            countMap = new Int2IntArrayMap();
            for (int[] ints : solutions) {
                sourceFeature = ints[posIndex];
                int feature;
                for (int i = 1; i <= sourceFeature; i <<= 1) {
                    feature = sourceFeature & i;
                    if (feature == 0) continue;
                    int count = countMap.getOrDefault(feature, 0) + 1;
                    countMap.put(feature, count);
                    if (count > maxCount) {
                        mode = feature;
                        maxCount = count;
                    }
                }
            }
            finalSolution[posIndex] = mode;
        }

        return finalSolution;
    }

    /**
     *
     * @param candidateSolutions candidate solutions, note these candidate solutions each source feature only
     *                           contains one source
     * @param fragmentLength fragment length
     * @return solution, each element is a source feature (only contain one source), e.g., 1 or 2 or 4 or 8 or 16
     */
    public static int[] majorVote(List<IntList> candidateSolutions,
                                         int fragmentLength){

        int[] solution;
        List<int[]> solutions = new ArrayList<>();
        int sourceFeature, start, end;
        for (IntList candidateSolution : candidateSolutions) {
            solution = new int[fragmentLength];
            for (int k = 0; k < candidateSolution.size(); k = k + 3) {
                sourceFeature = candidateSolution.getInt(k);
                start = candidateSolution.getInt(k + 1); // inclusive
                end = candidateSolution.getInt(k + 2); // inclusice
                Arrays.fill(solution, start, end + 1, sourceFeature);
            }
            solutions.add(solution);
        }
        int[] finalSolution = new int[fragmentLength];
        Int2IntMap countMap;
        IntSet modeSet;
        for (int posIndex = 0; posIndex < fragmentLength; posIndex++) {
            int maxCount = -1;
            modeSet = new IntArraySet();
            countMap = new Int2IntArrayMap();
            for (int[] ints : solutions) {
                sourceFeature = ints[posIndex];
                int feature;
                for (int i = 1; i <= sourceFeature; i <<= 1) {
                    feature = sourceFeature & i;
                    if (feature == 0) continue;
                    int count = countMap.getOrDefault(feature, 0) + 1;
                    countMap.put(feature, count);
                    if (count > maxCount) {
                        maxCount = count;
                        modeSet.clear();
                        modeSet.add(feature);
                    }else if (count == maxCount){
                        modeSet.add(feature);
                    }
                }
            }
            if (modeSet.size() == 1){
                finalSolution[posIndex] = modeSet.iterator().nextInt();
            }else {
                // when source feature contain multiple source, it will be set to native ancestry
                finalSolution[posIndex] = 1;
            }
        }

        return finalSolution;
    }

    public static int getMiniOptimalSolutionSize(IntList[] solutions){
        int[] size = Solution.getOptimalSolutionsSize(solutions);
        int mini = Integer.MAX_VALUE;
        for (int j : size) {
            mini = Math.min(j, mini);
        }
        return mini;
    }

    public static int[] getOptimalSolutionsSize(IntList[] solutions){
        int[] sizes = new int[solutions.length];
        Arrays.fill(sizes, -1);
        int cumSize=1;
        EnumSet<Source> sources;
        for (int i = 0; i < solutions.length; i++) {
            for (int j = solutions[i].size()-1; j > 0; j=j-3) {
                sources = Source.getSourcesFrom(solutions[i].getInt(j-2));
                if (sources.size()==1) continue;
                cumSize*=sources.size();
            }
            sizes[i] = cumSize;
            cumSize = 1;
        }
        return sizes;
    }

    public static BitSet[] reverseSrcGenotype(BitSet[] srcGenotype, int seqLen){
        BitSet[] reversedSrcGenotype = new BitSet[srcGenotype.length];
        for (int i = 0; i < srcGenotype.length; i++) {
            reversedSrcGenotype[i] = new BitSet(seqLen);
        }

        for (int i = 0; i < srcGenotype.length; i++) {
            for (int j = 0; j < seqLen; j++) {
                reversedSrcGenotype[i].set(seqLen-1-j, srcGenotype[i].get(j));
            }
        }
        return reversedSrcGenotype;
    }

    /**
     *
     * @param genotype genotype
     * @return 反向序列
     */
    public static BitSet reverseGenotype(BitSet genotype, int seqLen){
        BitSet reverseGenotype = new BitSet(seqLen);
        for (int i = 0; i < seqLen; i++) {
            reverseGenotype.set(seqLen-1-i, genotype.get(i));
        }
        return reverseGenotype;
    }

    public static int getMiniSolutionEleCount(IntList[] solutions){
        int miniSolutionEleCount = Integer.MAX_VALUE;
        for (IntList solution : solutions) {
            miniSolutionEleCount = Math.min(solution.size(), miniSolutionEleCount);

        }
        return miniSolutionEleCount;
    }

    /**
     *
     * @param forwardCandidateSolutions forward candidate solutions, it consists of multiple solutions with the same
     *                                  mini cost score
     * dim1 is mini cost score index, dim2 is solution
     * Solution consists of multiple groups, every three numbers as a group, representing a tract
     * the first number is source population index, equal WindowSource.Source.index()
     * the second and third number is start(inclusive) position and end(inclusive) position
     * @return solution, each element is a source feature (only contain one source), e.g., 1 or 2 or 4 or 8 or 16
     */
    public static int[] coalescentForward(IntList[] forwardCandidateSolutions){
        int[] miniSolutionSizeArray = Solution.getOptimalSolutionsSize(forwardCandidateSolutions);
        int miniSolutionSize = Solution.getMiniOptimalSolutionSize(forwardCandidateSolutions);
        int miniSolutionEleCount = Solution.getMiniSolutionEleCount(forwardCandidateSolutions);
        int solutionEleCount;
        Set<IntList> solutionSet = new HashSet<>();
        for (int i = 0; i < forwardCandidateSolutions.length; i++) {
            if (miniSolutionSizeArray[i]!=miniSolutionSize) continue;
            if (forwardCandidateSolutions[i].size()!=miniSolutionEleCount) continue;
            solutionEleCount =  forwardCandidateSolutions[i].size();
            // filter Source is NATIVE
            if (solutionEleCount==3 && (forwardCandidateSolutions[i].getInt(0)==Source.NATIVE.getFeature())) continue;
            solutionSet.add(forwardCandidateSolutions[i]);
        }
        List<IntList> solutionList = new ArrayList<>(solutionSet);
        if (solutionList.size()==0) return new int[0];
        int[] targetSourceCumLen = Solution.getTargetSourceCumLen(solutionList);
        int maxTargetSourceCumLen = Integer.MIN_VALUE;
        for (int j : targetSourceCumLen) {
            maxTargetSourceCumLen = Math.max(j, maxTargetSourceCumLen);
        }
        List<IntList> solutionList2 = new ArrayList<>();
        for (int i = 0; i < targetSourceCumLen.length; i++) {
            if (targetSourceCumLen[i] == maxTargetSourceCumLen){
                solutionList2.add(solutionList.get(i));
            }
        }

        List<IntList> forwardSolutions = new ArrayList<>();
        IntList maxTargetSourceCumLenSolution, solutionRes;
        int fragmentLen = solutionList2.get(0).getInt(1)+1;
        IntList introgressedFeatureList = Source.getSingleSourceFeatureList();
        introgressedFeatureList.rem(1);
        for (IntList integers : solutionList2) {
            maxTargetSourceCumLenSolution = integers;
            solutionRes = new IntArrayList();
            int sourceFeature, start, end;
            for (int i = maxTargetSourceCumLenSolution.size() - 1; i > 0; i = i - 3) {
                sourceFeature = maxTargetSourceCumLenSolution.getInt(i - 2);
                start = maxTargetSourceCumLenSolution.getInt(i);
                end = maxTargetSourceCumLenSolution.getInt(i - 1);
//                if (introgressedFeatureList.contains(sourceFeature) && (end-start+1) < fragmentLen/5){
//                    sourceFeature = 1; // 片段过短, 可能是ILS, 因此设为native ancestry
//                }
                solutionRes.add(sourceFeature);
                solutionRes.add(start);
                solutionRes.add(end);
            }
            forwardSolutions.add(solutionRes);
        }

        return majorVote(forwardSolutions, fragmentLen);
    }

    /**
     *
     * @param reversedCandidateSolutions forward candidate solutions, it consists of multiple solutions with the same
     *                                   mini cost score
     * dim1 is mini cost score index, dim2 is solution
     * Solution consists of multiple groups, every three numbers as a group, representing a tract
     * the first number is source population index, equal WindowSource.Source.index()
     * the second and third number is start(inclusive) position and end(inclusive) position
     * @return solution, each element is a source feature (only contain one source), e.g., 1 or 2 or 4 or 8 or 16
     */
    public static int[] coalescentReverse(IntList[] reversedCandidateSolutions){
        int[] miniSolutionSizeArray = Solution.getOptimalSolutionsSize(reversedCandidateSolutions);
        int miniSolutionSize = Solution.getMiniOptimalSolutionSize(reversedCandidateSolutions);
        int miniSolutionEleCount = Solution.getMiniSolutionEleCount(reversedCandidateSolutions);
        int solutionEleCount;
        Set<IntList> solutionSet = new HashSet<>();
        for (int i = 0; i < reversedCandidateSolutions.length; i++) {
            if (miniSolutionSizeArray[i]!=miniSolutionSize) continue;
            if (reversedCandidateSolutions[i].size()!=miniSolutionEleCount) continue;
            solutionEleCount =  reversedCandidateSolutions[i].size();
            // filter Source is NATIVE
            if (solutionEleCount==3 && (reversedCandidateSolutions[i].getInt(0)==Source.NATIVE.getFeature())) continue;
            solutionSet.add(reversedCandidateSolutions[i]);
        }
        List<IntList> solutionList = new ArrayList<>(solutionSet);
        if (solutionList.size()==0) return new int[0];
        int[] targetSourceCumLen = Solution.getTargetSourceCumLen(solutionList);
        int maxTargetSourceCumLen = Integer.MIN_VALUE;
        for (int j : targetSourceCumLen) {
            maxTargetSourceCumLen = Math.max(j, maxTargetSourceCumLen);
        }

        List<IntList> solutionList2 = new ArrayList<>();
        for (int i = 0; i < targetSourceCumLen.length; i++) {
            if (targetSourceCumLen[i] == maxTargetSourceCumLen){
                solutionList2.add(solutionList.get(i));
            }
        }
        List<IntList> reverseSolutions = new ArrayList<>();
        IntList solutionRes;
        IntList maxTargetSourceCumLenSolution;
        int seqLen = solutionList2.get(0).getInt(1) + 1;
        IntList introgressedFeatureList = Source.getSingleSourceFeatureList();
        introgressedFeatureList.rem(1);
        for (IntList integers : solutionList2) {
            solutionRes = new IntArrayList();
            maxTargetSourceCumLenSolution = integers;
            int sourceFeature, start, end;
            for (int i = 0; i < maxTargetSourceCumLenSolution.size(); i = i + 3) {
                sourceFeature = maxTargetSourceCumLenSolution.getInt(i);
                start = seqLen - 1 - maxTargetSourceCumLenSolution.getInt(i + 1);
                end = seqLen - 1 - maxTargetSourceCumLenSolution.getInt(i + 2);
//                if (introgressedFeatureList.contains(sourceFeature) && (end-start+1) < seqLen/5){
//                    sourceFeature = 1; // 片段过短, 可能是ILS, 因此设为native ancestry
//                }
                solutionRes.add(sourceFeature);
                solutionRes.add(start);
                solutionRes.add(end);
            }
            reverseSolutions.add(solutionRes);
        }

        return majorVote(reverseSolutions, seqLen);
    }

    public static int[] getTargetSourceCumLen(List<IntList> solutions){
        int[] cumLen = new int[solutions.size()];
        IntList singleSourceFeatureList = Source.getSingleSourceFeatureList();
        for (int i = 0; i < solutions.size(); i++) {
            for (int j = 0; j < solutions.get(i).size(); j=j+3) {
                if (singleSourceFeatureList.contains(solutions.get(i).getInt(j))){
                    cumLen[i]+=(solutions.get(i).getInt(j+1)) - (solutions.get(i).getInt(j+2));
                }
            }
        }
        return cumLen;
    }
}
