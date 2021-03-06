/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.AccountingDocumentValidationBase;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validates that, for accounting lines which are identified as capital asset accounting lines,
 * they are unique so they can be handled individually by the Capital Asset Builder module.
 */
public class CapitalAssetAccountingLineUniquenessEnforcementValidation extends AccountingDocumentValidationBase {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        Collection<AccountingLine> capitalAssetLines = getCapitalAssetAccountingLines( getAllAccountingLines() );
        // no matching lines, nothing to validate
        if ( capitalAssetLines.isEmpty() ) {
            return true;
        }
        return validateLineUniqueness( capitalAssetLines );
    }


    protected Collection<String> getCapitalAssetObjectSubTypes() {
        return getParameterService().getParameterValuesAsString(
                KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class,
                "FINANCIAL_PROCESSING_CAPITAL_OBJECT_SUB_TYPES");
        // JHK: I don't like the use of the constant above - but this parameter exists in the CAB module
        // Fortunately, since Rice 2.0 - the statement above will not blow up if CAB is not installed.
        // It will just return an empty list which will short-circuit the validation.
    }

    protected Collection<AccountingLine> getCapitalAssetAccountingLines( Collection<AccountingLine> allLines ) {
        Collection<AccountingLine> capitalAssetAccountingLines = new ArrayList<AccountingLine>(allLines.size());
        Collection<String> capitalAssetObjectSubTypes = getCapitalAssetObjectSubTypes();
        // if none defined, exit
        if ( capitalAssetObjectSubTypes.isEmpty() ) {
            return capitalAssetAccountingLines;
        }
        for ( AccountingLine line : allLines ) {
            // we only care about expenses (not encumbrance or budget)
            // many documents leave the balance type blank when going to use the "default" of "AC"
            if ( StringUtils.isNotBlank( line.getBalanceTypeCode() ) && !StringUtils.equals( line.getBalanceTypeCode(), KFSConstants.BALANCE_TYPE_ACTUAL ) ) {
                continue;
            }
            // need to ensure that we have a financial object to test the sub type against - only refresh if necessary
            if ( StringUtils.isBlank(line.getFinancialObjectCode()) ) {
                continue; // no object code
            }
            if ( ObjectUtils.isNull( line.getObjectCode() )
                    || !StringUtils.equals(line.getChartOfAccountsCode(), line.getObjectCode().getChartOfAccountsCode())
                    || !StringUtils.equals(line.getFinancialObjectCode(), line.getObjectCode().getFinancialObjectCode()) ) {
                line.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            }
            if ( ObjectUtils.isNull( line.getObjectCode() ) ) {
                continue; // invalid object code
            }
            if ( capitalAssetObjectSubTypes.contains( line.getObjectCode().getFinancialObjectSubTypeCode() ) ) {
                capitalAssetAccountingLines.add( line );
            }
        }
        return capitalAssetAccountingLines;
    }

    protected boolean validateLineUniqueness( Collection<AccountingLine> capitalAssetAccountingLines ) {
        HashSet<String> uniqueLineKeys = new HashSet<String>();
        for ( AccountingLine line : capitalAssetAccountingLines ) {
            if ( !uniqueLineKeys.add( getLineUniquenessKey(line) ) ) {
                GlobalVariables.getMessageMap().putError("sourceAccountingLines", KFSKeyConstants.ERROR_NONUNIQUE_CAPITAL_ASSET_ACCOUNTING_LINE);
                return false;
            }
        }
        return true;
    }

    protected String getLineUniquenessKey( AccountingLine line ) {
        StringBuilder sb = new StringBuilder();
        sb.append( line.getChartOfAccountsCode() );
        sb.append( line.getAccountNumber() );
        sb.append( line.getSubAccountNumber() );
        sb.append( line.getFinancialObjectCode() );
        sb.append( line.getFinancialSubObjectCode() );
        sb.append( line.getProjectCode() );
        sb.append( line.getOrganizationReferenceId() );
        return sb.toString();
    }

}
