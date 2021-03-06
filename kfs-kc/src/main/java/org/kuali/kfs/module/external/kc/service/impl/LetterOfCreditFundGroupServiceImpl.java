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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.LetterOfCreditFundGroup;
import org.kuali.kfs.module.external.kc.dto.AwardBasisOfPaymentDTO;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.webService.AwardPaymentWebSoapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kra.external.awardpayment.AwardPaymentWebService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 05:29:28 HST 2010
 * Generated source version: 2.2.10
 *
 */

public class LetterOfCreditFundGroupServiceImpl implements ExternalizableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LetterOfCreditFundGroupServiceImpl.class);

    protected ConfigurationService configurationService;

    protected AwardPaymentWebService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        AwardPaymentWebService awardPaymentWebService = (AwardPaymentWebService) GlobalResourceLoader.getService(KcConstants.AwardPayment.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (awardPaymentWebService == null) {
            LOG.warn("Couldn't get AwardWebService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");
            AwardPaymentWebSoapService ss =  null;
            try {
                ss = new AwardPaymentWebSoapService();
            }
            catch (MalformedURLException ex) {
                LOG.error("Could not intialize AwardPaymentWebSoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize AwardPaymentWebSoapService: " + ex.getMessage());
            }
            awardPaymentWebService = ss.getAwardPaymentWebServicePort();
        }

        return awardPaymentWebService;
    }

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        //use the proposal number as its the awardId on the KC side.
        AwardBasisOfPaymentDTO dto  = this.getWebService().getBasisOfPayment((String) primaryKeys.get("letterOfCreditFundGroupCode"));
        return fundGroupFromDTO(dto);
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        AwardBasisOfPaymentDTO criteria = new AwardBasisOfPaymentDTO();
        List<AwardBasisOfPaymentDTO> result = null;
        criteria.setBasisOfPaymentCode((String) fieldValues.get("letterOfCreditFundGroupCode"));
        criteria.setDescription((String) fieldValues.get("letterOfCreditFundGroupDescription"));

        try {
          result  = this.getWebService().getMatchingBasisOfPayments(criteria);
        } catch (WebServiceException ex) {
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, getConfigurationService().getPropertyValueAsString(KFSConstants.KC_APPLICATION_URL_KEY));
        }

        List<LetterOfCreditFundGroup> basises = new ArrayList<LetterOfCreditFundGroup>();
        if (result != null) {
            for (AwardBasisOfPaymentDTO dto : result) {
                basises.add(fundGroupFromDTO(dto));
            }
        }
        return basises;
    }

    protected LetterOfCreditFundGroup fundGroupFromDTO(AwardBasisOfPaymentDTO basis) {
        LetterOfCreditFundGroup fundGroup = new LetterOfCreditFundGroup();
        fundGroup.setLetterOfCreditFundGroupCode(basis.getBasisOfPaymentCode());
        fundGroup.setLetterOfCreditFundGroupDescription(basis.getDescription());
        return fundGroup;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

 }
