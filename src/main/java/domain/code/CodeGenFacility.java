package domain.code;

/**
 *
 */
public interface CodeGenFacility {

    /**
     * This allows new code "mappings" to be added, to maintain separation between Application KlondikeDomain
     * and the arbitrary language used for L1.
     *
     * This abstracts over repositories (i.e., Gammas)
     */
    <C>CodeGenFacility addCodeGenerator(C codegen);
}
