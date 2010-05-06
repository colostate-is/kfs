/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionDocumentService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule;
import org.kuali.kfs.module.endow.document.validation.DeleteTransactionLineRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.AbstractKualiDecimal;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;

public class EndowmentTransactionLinesDocumentBaseRules extends EndowmentTransactionalDocumentBaseRule implements AddTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine>, DeleteTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine> {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.DeleteTransactionLineRule#processDeleteTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public boolean processDeleteTransactionLineRules(EndowmentTransactionLinesDocument EndowmentTransactionLinesDocument, EndowmentTransactionLine EndowmentTransactionLine) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument transLine, EndowmentTransactionLine line) 
    {
        boolean isValid = true; 
        isValid &= !GlobalVariables.getMessageMap().hasErrors(); 
        
        //Obtain Prefix for Error fields in UI.
        String ERROR_PREFIX = null;
        if( line instanceof EndowmentSourceTransactionLine)
            ERROR_PREFIX = EndowPropertyConstants.SOURCE_TRANSACTION_LINE_PREFIX;
        else
            ERROR_PREFIX = EndowPropertyConstants.TARGET_TRANSACTION_LINE_PREFIX;            
            
        if(isValid)
        {
            //General not null validation for KemID, Etran and Income/Principal DropDown.
            if(!SpringContext.getBean(DictionaryValidationService.class).isBusinessObjectValid(line, null))
                return false;    
            
            //Validate KemID
            if(!validateKemId(line,ERROR_PREFIX))
                return false;
         
            //Active Kemid
            isValid &= isActiveKemId(line,ERROR_PREFIX);
            
            //Validate no restriction transaction restriction
            isValid &= validateNoTransactionRestriction(line,ERROR_PREFIX);
            
            //Validate ETran code
            if(!validateEndowmentTransactionCode(line,ERROR_PREFIX))
                return false;

            //Validate GL object Code
            isValid &=  SpringContext.getBean(EndowmentTransactionDocumentService.class).matchChartBetweenKEMIDAndETranCode(line.getKemid(), line.getEtranCode(), line.getTransactionIPIndicatorCode());
            
            //Set Corpus Indicator  
            line.setCorpusIndicator(SpringContext.getBean(EndowmentTransactionLinesDocumentService.class).getCorpusIndicatorValueforAnEndowmentTransactionLine(line.getKemid(), line.getEtranCode(), line.getTransactionIPIndicatorCode()));
            
            //Refresh all references for the given KemId
            //line.getKemidObj().refreshNonUpdateableReferences();

            //Validate ETran code as E or I
            isValid &= validateEndowmentTransactionTypeCode(line,ERROR_PREFIX);
            
            //Validate Greater then Zero(thus positive) value
            isValid &= validateTransactionAmountGreaterThanZero(line,ERROR_PREFIX);
            
            //Validate Units is Greater then Zero(thus positive) value
            isValid &= validateTransactionUnitsGreaterThanZero(line,ERROR_PREFIX);
            
            //Validate if a KEMID can have a principal transaction when IP indicator is P
            isValid &= canKEMIDHaveAPrincipalTransaction(line,ERROR_PREFIX);
        } 
        
        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * This method validates the KEMID code. 
     * 
     * @param tranSecurity
     * @return
     */
    protected boolean isKemIdCodeEmpty(EndowmentTransactionLine line, String prefix)
    {
        if( StringUtils.isEmpty(line.getKemid()) )
        {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_REQUIRED);
            return true;        
        }
        
        return false;
    }
    
    /**
     * This method validates the KemId code and tries to create a KEMID object from the code.
     * 
     * @param line
     * @return
     */
    protected boolean validateKemId(EndowmentTransactionLine line,String prefix)
    {
        boolean success = true;
        
        KEMID kemId = (KEMID) SpringContext.getBean(KEMIDService.class).getByPrimaryKey(line.getKemid());
        line.setKemidObj(kemId);
        if( null == kemId )
        {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_INVALID);
            success = false;
        }

        return success;
    }

    /**
     * This method determines if the KEMID is active. 
     * 
     * @param line
     * @return
     */
    protected boolean isActiveKemId(EndowmentTransactionLine line,String prefix)
    {
        if(line.getKemidObj().isClose())
        {
            putFieldError(prefix + EndowPropertyConstants.KEMID , EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_INACTIVE);
            return false;
            
        }
        else
        {
            return true;
        }
    }
    
    /**
     * This method checks if the KEMID restriction code is "NTRAN"
     * 
     * @param line
     * @return
     */
    protected boolean validateNoTransactionRestriction(EndowmentTransactionLine line,String prefix)
    {
        if(line.getKemidObj().getTransactionRestrictionCode().equalsIgnoreCase(EndowConstants.TransactionRestrictionCode.TRAN_RESTR_CD_NTRAN))
        {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_NO_TRAN_CODE);
            return false;
        }
        else
        {
            return true;
        }
            
    }
    
    /**
     * This method checks is the Transaction amount entered is greater than Zero. 
     * 
     * @param line
     * @return
     */
    protected boolean validateTransactionAmountGreaterThanZero(EndowmentTransactionLine line, String prefix)
    {
        if(line.getTransactionAmount().isGreaterThan(AbstractKualiDecimal.ZERO))
            return true;
        else
        {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_AMOUNT_GREATER_THAN_ZERO);
            return false;
        }
    }
    
    /**
     * This method checks is the Transaction Units entered is greater than Zero. 
     * 
     * @param line
     * @return
     */
    protected boolean validateTransactionUnitsGreaterThanZero(EndowmentTransactionLine line, String prefix)
    {
        if(line.getTransactionUnits().isGreaterThan(AbstractKualiDecimal.ZERO))
            return true;
        else
        {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_UNITS_GREATER_THAN_ZERO);
            return false;
        }
    }
    
    /**
     * This method checks is the Transaction Units & Amount entered are equal. 
     * 
     * @param line
     * @param prefix
     * @return
     */
    protected boolean validateTransactionUnitsAmountEqual(EndowmentTransactionLine line, String prefix)
    {
        if(line.getTransactionUnits().compareTo(line.getTransactionAmount()) == 0)
            return true;
        else
        {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_AMOUNT_UNITS_EQUAL);
            return false;
        }
    }

    /**
     * This method checks if the ETRAN code has a type code of E or I. 
     * 
     * @param line
     * @return
     */
    protected boolean validateEndowmentTransactionTypeCode(EndowmentTransactionLine line,String prefix)
    {
        if(line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE) || line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE) )
            return true;
        else
        {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ENDOWMENT_TRANSACTION_TYPE_CODE_VALIDITY);
            return false;
        }
    }
    
    /**
     * This method validates the EndowmentTransaction code. 
     * 
     * @param tranSecurity
     * @return
     */
    protected boolean isEndowmentTransactionCodeEmpty(EndowmentTransactionLine line,String prefix)
    {
        if( StringUtils.isEmpty(line.getEtranCode()) )
        {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_REQUIRED);
            return true;
        }
        
        return false;
    }
    
    /**
     * This method validates the EndowmentTransaction code and tries to create a EndowmentTransactionCode object from the code.
     * 
     * @param line
     * @return
     */
    protected boolean validateEndowmentTransactionCode(EndowmentTransactionLine line,String prefix)
    {
        boolean success = true;
        
        EndowmentTransactionCode etran = (EndowmentTransactionCode) SpringContext.getBean(EndowmentTransactionCodeService.class).getByPrimaryKey(line.getEtranCode());
        line.setEtranCodeObj(etran);
        if( null == etran )
        {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_INVALID);
            success = false;
        }

        return success;
    }
    
    /**
     * This method validates if a KEMID can have a principal transaction when IP indicator is equal to P.
     * 
     * @param line
     * @return
     */
    protected boolean canKEMIDHaveAPrincipalTransaction(EndowmentTransactionLine line, String prefix){
        boolean canHaveTransaction = true;
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode)){
            String kemid = line.getKemid();
            if (!SpringContext.getBean(EndowmentTransactionLinesDocumentService.class).canKEMIDHaveAPrincipalActivity(kemid, ipIndicatorCode)){
                putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_CAN_NOT_HAVE_A_PRINCIPAL_TRANSACTION);
                canHaveTransaction = false;
            }
        }
        return canHaveTransaction;
    }
    
    protected boolean templateMethod(EndowmentTransactionLine line)
    {
        boolean success = true;
        
        return success;
    }
    
}
