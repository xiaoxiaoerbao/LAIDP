package utils;

/**
 * modified from BiSNP, extend from RefChrPos
 */
public class SNP extends ChrPos {

    /**
     * The reference allele
     */
    public Allele reference = null;
    /**
     * The alternative allele
     */
    public Allele alternative = null;

    public String snpID = null;
    /**
     * Annotation information of SNP
     */
    public String info = null;

    /**
     * Construct an object with all fields initialized
     * @param chr
     * @param pos
     * @param refBase
     * @param altBase
     * @param info
     */
    public SNP(String chr, int pos, char refBase, char altBase, String snpID, String info) {
        super(chr, pos);
        this.initializeRefAllele(refBase);
        this.initializeAltAllele(altBase);
        this.SetSNP_ID(snpID);
        this.setSNPInfo(info);

    }

    /**
     * Initialize the reference allele
     * @param refBase
     */
    private void initializeRefAllele(char refBase) {
        this.reference = new Allele (refBase);
    }

    /**
     * Initialize the alternative allele
     * @param altBase
     */
    private void initializeAltAllele(char altBase) {
        this.alternative = new Allele (altBase);
    }


    /**
     * Return the byte value of the reference allele
     * @return
     */
    public byte getReferenceAlleleByte() {
        return reference.getAlleleByte();
    }

    /**
     * Return the base of the reference allele
     * @return
     */
    public char getReferenceAlleleBase() {
        return AlleleEncoder.getAlleleBaseFromByte(this.getReferenceAlleleByte());
    }
    /**
     * Return the byte value of the alternative allele
     * @return
     */
    public byte getAlternativeAlleleByte() {
        return alternative.getAlleleByte();
    }

    /**
     * Return the base of the alternative allele
     * @return
     */
    public char getAlternativeAlleleBase () {
        return AlleleEncoder.getAlleleBaseFromByte(this.getAlternativeAlleleByte());
    }


    /**
     *
     * @return the snpID of current snp
     */
    public String getSnpID(){
        return this.snpID;
    }

    /**
     * set the snpID of current snp
     * @param snpID
     */
    public void SetSNP_ID(String snpID){
        this.snpID=snpID;
    }

    /**
     * Return the annotation information of the SNP
     * @return
     */
    public String getSNPInfo () {
        return this.info;
    }

    /**
     * Set the information of the current SNP
     * @param info
     */
    public void setSNPInfo (String info) {
        this.info = info;
    }

    /**
     * Make a copy of the current object, without allele feature replicated (initialized to 0)
     * @return
     */
    public SNP replicateWithoutFeature() {
        return new SNP(this.getChr(), this.getPos(), this.getReferenceAlleleBase(), this.getAlternativeAlleleBase(),
                this.getSnpID(), this.getSNPInfo());
    }
}
