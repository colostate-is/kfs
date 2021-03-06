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
package org.kuali.kfs.module.bc;

import static org.kuali.kfs.module.bc.BCConstants.Report.BuildMode.BCAF;
import static org.kuali.kfs.module.bc.BCConstants.Report.BuildMode.MONTH;
import static org.kuali.kfs.module.bc.BCConstants.Report.BuildMode.PBGL;
import static org.kuali.kfs.module.bc.BCConstants.Report.ReportSelectMode.ACCOUNT;
import static org.kuali.kfs.module.bc.BCConstants.Report.ReportSelectMode.OBJECT_CODE;
import static org.kuali.kfs.module.bc.BCConstants.Report.ReportSelectMode.REASON;
import static org.kuali.kfs.module.bc.BCConstants.Report.ReportSelectMode.SUBFUND;

import java.util.EnumSet;

import org.kuali.kfs.module.bc.BCConstants.Report.BuildMode;
import org.kuali.kfs.module.bc.BCConstants.Report.ReportSelectMode;

/**
 * Contains properties related to a budget construction report.
 */
public enum BudgetConstructionReportMode {
    ACCOUNT_FUNDING_DETAIL_REPORT("AccountFundingDetailReport", BCAF, OBJECT_CODE, "BudgetOrgAccountFundingDetail", true), 
    ACCOUNT_OBJECT_DETAIL_REPORT("AccountObjectDetailReport", PBGL, SUBFUND, "BudgetOrgAccountObjectDetail", true),
    ACCOUNT_SUMMARY_REPORT("AccountSummaryReport", PBGL, SUBFUND, "BudgetOrgAccountSummary", true), 
    LEVEL_SUMMARY_REPORT("LevelSummaryReport", PBGL, SUBFUND, "BudgetOrgLevelSummary", true),
    MONTH_SUMMARY_REPORT("MonthSummaryReport", MONTH, SUBFUND, "BudgetOrgMonthSummary", true),
    OBJECT_SUMMARY_REPORT("ObjectSummaryReport", PBGL, SUBFUND, "BudgetOrgObjectSummary", true),    
    POSITION_FUNDING_DETAIL_REPORT("PositionFundingDetailReport", BCAF, OBJECT_CODE, "BudgetOrgPositionFundingDetail", true), 
    REASON_STATISTICS_REPORT("ReasonStatisticsReport", BCAF, REASON, "BudgetOrgReasonStatistics", false),
    REASON_SUMMARY_REPORT("ReasonSummaryReport", BCAF, REASON, "BudgetOrgReasonSummary", false), 
    SALARY_STATISTICS_REPORT("SalaryStatisticsReport", BCAF, OBJECT_CODE, "BudgetOrgSalaryStatistics", true),
    SALARY_SUMMARY_REPORT("SalarySummaryReport", BCAF, OBJECT_CODE, "BudgetOrgSalarySummary", false), 
    SUBFUND_SUMMARY_REPORT("SubFundSummaryReport", PBGL, SUBFUND, "BudgetOrgSubFundSummary", true),
    SYNCHRONIZATION_PROBLEMS_REPORT("SynchronizationProblemsReport", PBGL, ACCOUNT, "BudgetOrgSynchronizationProblems", true), 
    TWOPLG_LIST_REPORT("TwoPLGListReport", PBGL, ACCOUNT, "BudgetOrgTwoPLGList", true),
    ACCOUNT_EXPORT("AccountExport", PBGL, SUBFUND, true),
    MONTHLY_EXPORT("MonthlyExport", MONTH, SUBFUND, true),
    FUNDING_EXPORT("FundingExport", BCAF, SUBFUND, true);


    public final String reportModeName;
    public final BuildMode reportBuildMode;
    public final ReportSelectMode reportSelectMode;
    public final String jasperFileName;
    public final boolean lockThreshold;
    public final boolean export;

    /**
     * Constructs a BudgetConstructionReportMode.java.
     */
    private BudgetConstructionReportMode(final String reportModeName, final BuildMode reportBuildMode, final ReportSelectMode reportSelectMode, final String jasperFileName, final boolean lockThreshold) {
        this.reportModeName = reportModeName;
        this.reportBuildMode = reportBuildMode;
        this.reportSelectMode = reportSelectMode;
        this.jasperFileName = jasperFileName;
        this.lockThreshold = lockThreshold;
        this.export = false;
    }
    
    /**
     * Constructs a BudgetConstructionReportMode.java.
     */
    private BudgetConstructionReportMode(final String reportModeName, final BuildMode reportBuildMode, final ReportSelectMode reportSelectMode, final boolean export) {
        this.reportModeName = reportModeName;
        this.reportBuildMode = reportBuildMode;
        this.reportSelectMode = reportSelectMode;
        this.lockThreshold = false;
        this.export = export;
        this.jasperFileName = "";
    }

    /**
     * Returns the BudgetConstructionReportMode with name that matches given report mode name.
     * 
     * @param reportModeName - report name to find BudgetConstructionReportMode for
     * @return BudgetConstructionReportMode if found, or null
     */
    public static BudgetConstructionReportMode getBudgetConstructionReportModeByName(String reportModeName) {
        BudgetConstructionReportMode foundReportMode = null;
        
        for(BudgetConstructionReportMode reportMode : EnumSet.allOf(BudgetConstructionReportMode.class)) {
            if (reportMode.reportModeName.equals(reportModeName)) {
                foundReportMode = reportMode;
                break;
            }
        }
        
        return foundReportMode;
    }
}
