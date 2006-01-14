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
package org.kuali.module.chart.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.dao.OrganizationDao;
import org.springframework.orm.ojb.PersistenceBrokerTemplate;

/**
 * This class is the OJB implementation of the OrganizationDao interface.
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
*/
public class OrganizationDaoOjb extends PersistenceBrokerTemplate implements OrganizationDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationDaoOjb.class);
    /* (non-Javadoc)
     * @see org.kuali.dao.OrganizationDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Org getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("organizationCode",organizationCode);

        return (Org) getObjectByQuery(QueryFactory.newQuery(Org.class,criteria));
    }
    /* (non-Javadoc)
     * @see org.kuali.dao.OrganizationDao#save(org.kuali.bo.Org)
     */
    public void save(Org organization) {
        this.store(organization);
    }

}
