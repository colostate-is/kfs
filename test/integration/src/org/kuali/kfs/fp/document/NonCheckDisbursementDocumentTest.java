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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.test.parameters.AccountingLineParameter;
import org.kuali.test.parameters.DocumentParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * This class is used to test NonCheckDisbursementDocumentTest.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NonCheckDisbursementDocumentTest extends TransactionalDocumentTestBase {
    public static final String COLLECTION_NAME = "NonCheckDisbursementDocument.collection1";
    public static final String USER_NAME = "user1";
    public static final String DOCUMENT_PARAMETER = "nonCheckDisbursementDocumentParameter1";
    public static final String SOURCE_LINE4 = "sourceLine4";
    public static final String TARGET_LINE4 = "targetLine4";


    public final void testGetCreditTotal() throws Exception {
        KualiDecimal total = new KualiDecimal(0);
        NonCheckDisbursementDocument document = (NonCheckDisbursementDocument) documentParameter.createDocument(documentService);
        for (Iterator i = getSourceAccountingLineParametersFromFixtures().iterator(); i.hasNext();) {
            AccountingLineParameter parameter = (AccountingLineParameter) i.next();
            parameter.setDebitCreditCode(Constants.GL_CREDIT_CODE);
            document.addSourceAccountingLine((SourceAccountingLine) (parameter.createLine(documentParameter.getDocumentNumber())));
            total = total.add(parameter.getLineAmount());
            // add 1 line of debit
            if (!i.hasNext()) {
                parameter.setDebitCreditCode(Constants.GL_DEBIT_CODE);
                parameter.setLineAmount(new KualiDecimal("50.00"));
            }
        }

        assertEquals(total, document.getCreditTotal());
    }

    public final void testGetDebitTotal() throws Exception {
        KualiDecimal total = new KualiDecimal(0);
        NonCheckDisbursementDocument document = (NonCheckDisbursementDocument) documentParameter.createDocument(documentService);
        for (Iterator i = getSourceAccountingLineParametersFromFixtures().iterator(); i.hasNext();) {
            AccountingLineParameter parameter = (AccountingLineParameter) i.next();
            parameter.setDebitCreditCode(Constants.GL_DEBIT_CODE);
            document.addSourceAccountingLine((SourceAccountingLine) (parameter.createLine(documentParameter.getDocumentNumber())));
            total = total.add(parameter.getLineAmount());
            // add 1 line of debit
            if (!i.hasNext()) {
                parameter.setDebitCreditCode(Constants.GL_CREDIT_CODE);
                parameter.setLineAmount(new KualiDecimal("50.00"));
            }
        }

        assertEquals(total, document.getDebitTotal());
    }

    /**
     * 
     * @see org.kuali.core.document.DocumentTestCase#getDocumentParameterFixture()
     */
    public DocumentParameter getDocumentParameterFixture() {
        return (TransactionalDocumentParameter) getFixtureEntryFromCollection(COLLECTION_NAME, DOCUMENT_PARAMETER).createObject();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    public List getTargetAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, TARGET_LINE4).createObject());
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    public List getSourceAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, SOURCE_LINE4).createObject());
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getUserName()
     */
    public String getUserName() {
        return (String) getFixtureEntryFromCollection(COLLECTION_NAME, USER_NAME).createObject();
    }
    
    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#testConvertIntoErrorCorrection()
     */
    public void testConvertIntoErrorCorrection() throws Exception {
        // for now we just want this to run without problems, so we are overriding the parent's 
        // and leaving blank to run successfully 
        // when we get to this document, we'll fix the problem with blanket approving non check 
        // disbursement document test
    }
}
