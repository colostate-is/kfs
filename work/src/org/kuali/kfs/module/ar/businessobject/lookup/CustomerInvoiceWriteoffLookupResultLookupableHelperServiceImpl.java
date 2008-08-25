/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.web.ui.CustomerInvoiceWriteoffLookupResultRow;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.AuthorizationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.comparator.BeanPropertyComparator;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;

public class CustomerInvoiceWriteoffLookupResultLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private CustomerInvoiceWriteoffDocumentService customerInvoiceWriteoffDocumentService;

    private static final Log LOG = LogFactory.getLog(CustomerInvoiceWriteoffLookupResultLookupableHelperServiceImpl.class);

    /**
     * This method performs the lookup and returns a collection of lookup items
     * 
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection<BusinessObject> displayList;

        // call search method to get results
        if (bounded) {
            displayList = (Collection<BusinessObject>) getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = (Collection<BusinessObject>) getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }

        // iterate through result list and wrap rows with return url and action urls
        for (BusinessObject element : displayList) {
            LOG.debug("Doing lookup for " + element.getClass());
            String returnUrl = getReturnUrl(element, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName());

            //add list of customer invoice document sub rows
            List<ResultRow> subResultRows = new ArrayList<ResultRow>();
            for (CustomerInvoiceDocument customerInvoiceDocument : ((CustomerInvoiceWriteoffLookupResult) element).getCustomerInvoiceDocuments()) {

                List<Column> subResultColumns = new ArrayList<Column>();
                
                for (String propertyName : new String[] { "documentNumber", "age" }) {
                    subResultColumns.add(setupResultsColumn(customerInvoiceDocument, propertyName));
                }
                
                ResultRow subResultRow = new ResultRow((List<Column>) subResultColumns, "", "", "");
                subResultRow.setObjectId(customerInvoiceDocument.getObjectId());
                subResultRows.add(subResultRow);
            }
            
            Collection<Column> columns = getColumns(element);
            CustomerInvoiceWriteoffLookupResultRow row = new CustomerInvoiceWriteoffLookupResultRow((List<Column>) columns, subResultRows, returnUrl, getActionUrls(element), "");
            resultTable.add(row);
        }

        return displayList;
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        
        // TODO Use service to populate searchCollectionResults
        Collection searchResultsCollection = customerInvoiceWriteoffDocumentService.getCustomerInvoiceDocumentsForInvoiceWriteoffLookup();

        return this.buildSearchResultList(searchResultsCollection, new Long(searchResultsCollection.size()));
    }

    /**
     * build the search result list from the given collection and the number of all qualified search results
     * 
     * @param searchResultsCollection the given search results, which may be a subset of the qualified search results
     * @param actualSize the number of all qualified search results
     * @return the serach result list with the given results and actual size
     */
    protected List buildSearchResultList(Collection searchResultsCollection, Long actualSize) {
        CollectionIncomplete results = new CollectionIncomplete(searchResultsCollection, actualSize);

        // sort list if default sort column given
        List searchResults = (List) results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    /**
     * @param element
     * @param attributeName
     * @return Column
     */
    protected Column setupResultsColumn(BusinessObject element, String attributeName) {
        Column col = new Column();

        col.setPropertyName(attributeName);
        

        String columnTitle = getDataDictionaryService().getAttributeLabel(element.getClass(), attributeName);
        if (StringUtils.isBlank(columnTitle)) {
            columnTitle = getDataDictionaryService().getCollectionLabel(element.getClass(), attributeName);
        }
        col.setColumnTitle(columnTitle);
        col.setMaxLength(getDataDictionaryService().getAttributeMaxLength(element.getClass(), attributeName));

        Class formatterClass = getDataDictionaryService().getAttributeFormatter(element.getClass(), attributeName);
        Formatter formatter = null;
        if (formatterClass != null) {
            try {
                formatter = (Formatter) formatterClass.newInstance();
                col.setFormatter(formatter);
            }
            catch (InstantiationException e) {
                LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
            }
            catch (IllegalAccessException e) {
                LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
            }
        }

        // pick off result column from result list, do formatting
        String propValue = KFSConstants.EMPTY_STRING;
        Object prop = ObjectUtils.getPropertyValue(element, attributeName);

        // set comparator and formatter based on property type
        Class propClass = null;
        try {
            PropertyDescriptor propDescriptor = PropertyUtils.getPropertyDescriptor(element, col.getPropertyName());
            if (propDescriptor != null) {
                propClass = propDescriptor.getPropertyType();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
        }

        // formatters
        if (prop != null) {
            // for Booleans, always use BooleanFormatter
            if (prop instanceof Boolean) {
                formatter = new BooleanFormatter();
            }

            if (formatter != null) {
                propValue = (String) formatter.format(prop);
            }
            else {
                propValue = prop.toString();
            }
        }

        // comparator
        col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
        col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

        // check security on field and do masking if necessary
        boolean viewAuthorized = SpringContext.getBean(AuthorizationService.class).isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getFinancialSystemUser(), element.getClass().getName(), col.getPropertyName());
        if (!viewAuthorized) {
            Mask displayMask = getDataDictionaryService().getAttributeDisplayMask(element.getClass().getName(), col.getPropertyName());
            propValue = displayMask.maskValue(propValue);
        }
        col.setPropertyValue(propValue);


        if (StringUtils.isNotBlank(propValue)) {
            col.setPropertyURL(getInquiryUrl(element, col.getPropertyName()));
        }
        return col;
    }


    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     * 
     * @param bo
     * @return Collection<Column>
     */
    protected Collection<Column> getColumns(BusinessObject bo) {
        Collection<Column> columns = new ArrayList<Column>();

        for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(bo.getClass())) {
            columns.add(setupResultsColumn(bo, attributeName));
        }
        return columns;
    }

    @Override
    /*
     * TODO Figure out what to really send here...
     */
    public List getReturnKeys() {
        return new ArrayList();
    }

    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }
    
    public CustomerInvoiceWriteoffDocumentService getCustomerInvoiceWriteoffDocumentService() {
        return customerInvoiceWriteoffDocumentService;
    }

    public void setCustomerInvoiceWriteoffDocumentService(CustomerInvoiceWriteoffDocumentService customerInvoiceWriteoffDocumentService) {
        this.customerInvoiceWriteoffDocumentService = customerInvoiceWriteoffDocumentService;
    }
    

}
