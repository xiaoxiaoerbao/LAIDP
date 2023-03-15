## Introduction

`LAIDP` is a software for local ancestry inference(LAI). Comparing with other software, LAPID has higher accuracy in 
inferring ancient admixture events (>500 generations) and no recombination rate map required.

## Cite

Citation information for LAIDP is coming soon.

## Input files

### Genotype file

Please note that the current version of the software only accepts haploid VCF files and does not allow missing genotype data. If you have diploid sample data, please first perform phasing and imputation using software such as Beagle, and then split the data into haploid format. We apologize for any inconvenience and appreciate your understanding.

### Taxa group file

The `Taxa group file` is a tab-separated file with three columns, namely `Taxon`, `PopulationName`, and `Source`. 

Here is an example of a **Two-way** admixture:

| Taxon | PopulationName | Source        |
|-------|----------------|---------------|
| tsk_0 | E              | ADMIXED       |
| tsk_1 | E              | ADMIXED       |
| tsk_2 | E              | ADMIXED       |
| tsk_3 | D              | NATIVE        |
| tsk_4 | D              | NATIVE        |
| tsk_5 | D              | NATIVE        |
| tsk_6 | C              | INTROGRESSED_1|
| tsk_7 | C              | INTROGRESSED_1|
| tsk_8 | C              | INTROGRESSED_1|



Here is an example of a **Three-way** admixture:

| Taxon | PopulationName | Source        |
|-------|----------------|---------------|
| tsk_0 | E              | ADMIXED       |
| tsk_1 | E              | ADMIXED       |
| tsk_2 | E              | ADMIXED       |
| tsk_3 | D              | NATIVE        |
| tsk_4 | D              | NATIVE        |
| tsk_5 | D              | NATIVE        |
| tsk_6 | C              | INTROGRESSED_1|
| tsk_7 | C              | INTROGRESSED_1|
| tsk_8 | C              | INTROGRESSED_1|
| tsk_9 | B              | INTROGRESSED_2|
| tsk_10 | B              | INTROGRESSED_2|
| tsk_11 | B              | INTROGRESSED_2|

Please note that the `Source` column can only take one of the following values: `ADMIXED`, `NATIVE`, `INTROGRESSED_1`, `INTROGRESSED_2`, `INTROGRESSED_3`, or `INTROGRESSED_4`. Also, the values in the `Source` column must be in the order of `ADMIXED`, `NATIVE`, `INTROGRESSED_1`, and they are case-sensitive.

### Ancestral allele file

The `Ancestral allele file` contains information on the ancestral allele of each variant. It has four columns: `pos`, `ref`, `alt`, and `ancestralState`.

Here is an example:

| Pos | Ref | Alt | AncestralState |
|-----|-----|-----|----------------|
| 2 | A | T | 0|
| 5 | C | A | 1|
| 7 | G | T | -9|

Please note that the ancestralState column can take one of the following values: `0` indicates that the ref allele is ancestral, `1` indicates that the alt allele is ancestral, and `-9` indicates that the ancestral allele is unknown

## Running

```
java -jar LAIDP.jar -g test.vcf -taxaGroup taxaGroup.txt -ancestral ancestralAllele.txt -out out.txt
```

## Output files

LAIDP generates only one output file, localAnc.txt. Below is an example of a two-way admixture file:


| pos | tsk_0 | tsk_1 | tsk_2 |
|-----|-------|-------|-------|
|2|1,0|0,1|1,0|
|5|1,0|0,1|1,0|
|7|1,0|0,1|1,0|

The `pos` column corresponds to the position of each variant, and the subsequent columns indicate the ancestry 
information for each sample. Ancestry information is represented as [NATIVE, INTROGRESSED_1, INTROGRESSED_2,...], where:

`1,0` represents native ancestry,
`0,1` represents INTROGRESSED_1 ancestry, and
`0,0,1`represents INTROGRESSED_2 ancestry.
...

## Parameter

| Option | Type | Description | Default |
|--------|------|-------------|---------|
| g | string |path of genotype file in vcf format| - |
| taxaGroup | string | path of taxa group file | - |
| ancestral | string or int[] |ancestral allele, string mean file path, int[] means indices of outgroup taxa | - |
| out | string | path of output file | - |
| w | int | window size in variants (not bp) | 200 |
| s | int | step size in variants (not bp) | 100 |
| conjunctionNum | int | the number of grid when define local introgressed interval | 2 |
| switchCost | float | switch cost score when haplotype switching occurs | 1.5 |
| maxSolution | int | upper bound of the candidate solution | 32 |




