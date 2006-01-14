<%@ include file="/jsp/core/tldHeader.jsp" %>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<c:set var="journalVoucherAttributes" value="${DataDictionary['KualiJournalVoucherDocument'].attributes}" />
<kul:documentPage showDocumentInfo="true" 
	documentTypeName="KualiJournalVoucherDocument" htmlFormAction="financialJournalVoucher" renderMultipart="true" showTabButtons="true">
		<html:hidden property="document.nextSourceLineNumber"/>
		<kul:hiddenDocumentFields />
        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
		<!-- JOURNAL VOUCHER SPECIFIC FIELDS -->
		<kul:tab tabTitle="Journal Voucher Details" defaultOpen="true" tabErrorKey="${Constants.EDIT_JOURNAL_VOUCHER_ERRORS}" >
	    	<div class="tab-container" align=center>
	    	<table cellpadding=0 class="datatable" summary="view/edit ad hoc recipients">
            <tbody>
              <tr>
                <td colspan=2 class="subhead"><span class="subhead-left"> Journal Voucher Details</span></td>
              </tr>
              <tr>
                <th width="35%" class="bord-l-b">
                  <div align="right">
                    <kul:htmlAttributeLabel attributeEntry="${journalVoucherAttributes.accountingPeriod}" useShortLabel="false" />
                    &nbsp;
                  </div>
                </th>
                <td class="datacell-nowrap">
                	   	<SCRIPT type="text/javascript">
						<!--
						    function submitForChangedAccountingPeriod() {
					    		document.forms[0].submit();
						    }
						//-->
						</SCRIPT>
                        <select name="selectedAccountingPeriod" onchange="submitForChangedAccountingPeriod()">
							<c:forEach items="${KualiForm.accountingPeriods}" var="accountingPeriod">
								<c:set var="accountingPeriodCompositeValue" value="${accountingPeriod.universityFiscalPeriodCode}${accountingPeriod.universityFiscalYear}" />
								<c:choose>
									<c:when test="${KualiForm.selectedAccountingPeriod==accountingPeriodCompositeValue}" >
										<option value='<c:out value="${accountingPeriodCompositeValue}"/>' selected="selected"><c:out value="${accountingPeriod.universityFiscalPeriodName}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${accountingPeriodCompositeValue}" />'><c:out value="${accountingPeriod.universityFiscalPeriodName}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						<NOSCRIPT>
						<html:submit value="refresh" alt="press this button to refresh the page after changing the accounting period" />
						</NOSCRIPT>
						<kul:lookup boClassName="org.kuali.module.chart.bo.AccountingPeriod" hideReturnLink="true" />
                </td>
              </tr>
              <tr>
                <th width="35%" class="bord-l-b">
                  <div align="right">
                  	<kul:htmlAttributeLabel attributeEntry="${journalVoucherAttributes.balanceTypeCode}" useShortLabel="false" />
                  	&nbsp;
                  </div>
                </th>
                <td class="datacell-nowrap">
                        <SCRIPT type="text/javascript">
						<!--
						    function submitForChangedBalanceType() {
					    		document.forms[0].submit();
						    }
						//-->
						</SCRIPT>
						<html:hidden property="originalBalanceType" value="${KualiForm.selectedBalanceType.code}" />
						<html:hidden property="selectedBalanceType.financialOffsetGenerationIndicator" />
		        		<select name="selectedBalanceType.code" onchange="submitForChangedBalanceType()">
							<c:forEach items="${KualiForm.balanceTypes}" var="balanceType">
							  <c:choose>
									<c:when test="${KualiForm.selectedBalanceType.code==balanceType.code}" >
										<option value='<c:out value="${balanceType.code}"/>' selected="selected"><c:out value="${balanceType.code}" /> - <c:out value="${balanceType.name}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${balanceType.code}" />'><c:out value="${balanceType.code}" /> - <c:out value="${balanceType.name}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						<NOSCRIPT>
						<html:submit value="refresh" alt="press this button to refresh the page after changing the balance type" />
						</NOSCRIPT>
						<kul:lookup boClassName="org.kuali.module.chart.bo.codes.BalanceTyp" fieldConversions="code:selectedBalanceType.code" lookupParameters="selectedBalanceType.code:code" />
                </td>
              </tr>
              <tr>
                <th width="35%" class="bord-l-b">
                    <div align="right">
                    	<kul:htmlAttributeLabel attributeEntry="${journalVoucherAttributes.reversalDate}" useShortLabel="false" />
                  		&nbsp;
                  	</div></th>
                <td class="datacell-nowrap"><kul:dateInputNoAttributeEntry property="document.reversalDate" maxLength="10" size="10" /></td>
              </tr>
            </tbody>
          </table>
    	</div>
		</kul:tab>
        <fin:voucherAccountingLines
            editingMode="${KualiForm.editingMode}"
            editableAccounts="${KualiForm.editableAccounts}"
            />
		<kul:generalLedgerPendingEntries/>
		<kul:notes/>
		<kul:adHocRecipients/>
		<kul:routeLog/>
		<kul:panelFooter/>
		<kul:documentControls transactionalDocument="true" />
</kul:documentPage>