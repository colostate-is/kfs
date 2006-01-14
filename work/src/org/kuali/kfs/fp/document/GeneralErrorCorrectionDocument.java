/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.document;

import org.kuali.Constants;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.module.chart.bo.AccountingPeriod;



/**
 * This is the business object that represents the GeneralErrorCorrectionDocument in Kuali. This is a transactional document that
 * will eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines:
 * from and to. From lines are the source lines, to lines are the target lines.
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class GeneralErrorCorrectionDocument extends TransactionalDocumentBase  {
    private static final long serialVersionUID = 5560938005488306376L;

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralErrorCorrectionDocument.class);

    private AccountingPeriod accountingPeriod;

    /**
     * Initializes the array lists and some basic info.
     */
    public GeneralErrorCorrectionDocument() {
        super();
    }

    /**
     * Gets the accountingPeriod attribute.
     * @return Returns the accountingPeriod.
     */
    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    /**
     * Sets the accountingPeriod attribute value.
     * @param accountingPeriod The accountingPeriod to set.
     */
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }
   
    /**
     * Overrides the base implementation to return "From".
     */
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     */
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.TO;
    }
}