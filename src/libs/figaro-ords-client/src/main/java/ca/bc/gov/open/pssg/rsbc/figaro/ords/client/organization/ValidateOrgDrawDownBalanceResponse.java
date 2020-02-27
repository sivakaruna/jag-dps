package ca.bc.gov.open.pssg.rsbc.figaro.ords.client.organization;

import ca.bc.gov.open.pssg.rsbc.figaro.ords.client.FigaroOrdsClientConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 *
 * Represents the Validate Facility Party Response
 *
 * @author carolcarpenterjustice
 */
@JacksonXmlRootElement(localName = "validateOrgDrawDownBalance")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidateOrgDrawDownBalanceResponse {

    private String validationResult;
    private int respCode;
    private String respMsg;

    /**
     *
     * ValidateOrgDrawDownBalanceResponse default constructor.
     *
     * @param validationResult
     * @param respCode
     * @param respMsg
     *
     * @author carolcarpenterjustice
     */
    private ValidateOrgDrawDownBalanceResponse(String validationResult, int respCode, String respMsg) {
        this.respCode = respCode;
        this.respMsg = respMsg;
        this.validationResult = validationResult;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public int getRespCode() {
        return respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public static ValidateOrgDrawDownBalanceResponse ErrorResponse(String errorMessage) {
        return new ValidateOrgDrawDownBalanceResponse(
                FigaroOrdsClientConstants.SERVICE_BOOLEAN_FALSE,
                FigaroOrdsClientConstants.SERVICE_FAILURE_CD,
                errorMessage);
    }

    public static ValidateOrgDrawDownBalanceResponse SuccessResponse(String validationResult, String respCodeStr, String respMsg) {
        return new ValidateOrgDrawDownBalanceResponse(validationResult, Integer.parseInt(respCodeStr), respMsg);
    }
}
