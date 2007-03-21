<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>

<%@ include file="/jsp/core/tldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiSalaryExpenseTransferDocument"
	htmlFormAction="laborSalaryExpenseTransfer" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields />
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
    <kul:employeeLookup />
	<ld:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" 
		optionalFields="positionNumber,payrollEndDateFiscalYear,payrollEndDateFiscalPeriodCode,payrollTotalHours">
        <jsp:attribute name="sourceImportRowOverride">
            Import from Labor Ledger
            <kul:balanceInquiryLookup
	            boClassName="org.kuali.module.labor.bo.LedgerBalance"
                actionPath="${Constants.GL_BALANCE_INQUIRY_ACTION}"
	            lookupParameters="user.personPayrollIdentifier:emplid"
                fieldConversions="chartOfAccountsCode:newSourceLine.chartOfAccountsCode"
		        hideReturnLink="false" />
        </jsp:attribute>
    </ld:accountingLines>
	<ld:laborLedgerPendingEntries />
	<kul:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls transactionalDocument="true" />
</kul:documentPage>
