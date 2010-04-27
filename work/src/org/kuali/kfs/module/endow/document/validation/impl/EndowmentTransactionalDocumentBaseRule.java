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

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.module.cam.document.service.AssetTransferService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

public class EndowmentTransactionalDocumentBaseRule extends TransactionalDocumentRuleBase 
{
    /**
    *
    * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field, but
    * applicable to the whole document).
    *
    * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
    *
    */
   protected void putGlobalError(String errorConstant) {
       if (!errorAlreadyExists(KNSConstants.DOCUMENT_ERRORS, errorConstant)) {
           GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KNSConstants.DOCUMENT_ERRORS, errorConstant);
       }
   }

   /**
    *
    * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field, but
    * applicable to the whole document).
    *
    * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
    * @param parameter - Replacement value for part of the error message.
    *
    */
   protected void putGlobalError(String errorConstant, String parameter) {
       if (!errorAlreadyExists(KNSConstants.DOCUMENT_ERRORS, errorConstant)) {
           GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KNSConstants.DOCUMENT_ERRORS, errorConstant, parameter);
       }
   }

   /**
    *
    * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field, but
    * applicable to the whole document).
    *
    * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
    * @param parameters - Array of replacement values for part of the error message.
    *
    */
   protected void putGlobalError(String errorConstant, String[] parameters) {
       if (!errorAlreadyExists(KNSConstants.DOCUMENT_ERRORS, errorConstant)) {
           GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KNSConstants.DOCUMENT_ERRORS, errorConstant, parameters);
       }
   }

   /**
    *
    * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
    * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
    *
    * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
    *        the UI.
    * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
    *
    */
   protected void putFieldError(String propertyName, String errorConstant) {
       if (!errorAlreadyExists( propertyName, errorConstant)) {
           GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath( propertyName, errorConstant);
       }
   }

   /**
    *
    * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
    * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
    *
    * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
    *        the UI.
    * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
    * @param parameter - Single parameter value that can be used in the message so that you can display specific values to the
    *        user.
    *
    */
   protected void putFieldError(String propertyName, String errorConstant, String parameter) {
       if (!errorAlreadyExists( propertyName, errorConstant)) {
           GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath( propertyName, errorConstant, parameter);
       }
   }

   /**
    *
    * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
    * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
    *
    * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
    *        the UI.
    * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
    * @param parameters - Array of strings holding values that can be used in the message so that you can display specific values
    *        to the user.
    *
    */
   protected void putFieldError(String propertyName, String errorConstant, String[] parameters) {
       if (!errorAlreadyExists(propertyName, errorConstant)) {
           GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath( propertyName, errorConstant, parameters);
       }
   }

   /**
    *
    * This method is a convenience method to add a property-specific document error to the global errors list. This method makes
    * sure that the correct prefix is added to the property name so that it will display correctly on maintenance documents.
    *
    * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
    *        the UI.
    * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
    * @param parameter - Single parameter value that can be used in the message so that you can display specific values to the
    *        user.
    *
    */
   protected void putDocumentError(String propertyName, String errorConstant, String parameter) {
       if (!errorAlreadyExists(MaintenanceDocumentRuleBase.DOCUMENT_ERROR_PREFIX + propertyName, errorConstant)) {
           GlobalVariables.getMessageMap().putError(MaintenanceDocumentRuleBase.DOCUMENT_ERROR_PREFIX + propertyName, errorConstant, parameter);
       }
   }

   /**
    *
    * This method is a convenience method to add a property-specific document error to the global errors list. This method makes
    * sure that the correct prefix is added to the property name so that it will display correctly on maintenance documents.
    *
    * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
    *        the UI.
    * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
    * @param parameters - Array of String parameters that can be used in the message so that you can display specific values to the
    *        user.
    *
    */
   protected void putDocumentError(String propertyName, String errorConstant, String[] parameters) {
       GlobalVariables.getMessageMap().putError(MaintenanceDocumentRuleBase.DOCUMENT_ERROR_PREFIX + propertyName, errorConstant, parameters);
   }
   
   /**
   *
   * Convenience method to determine whether the field already has the message indicated.
   *
   * This is useful if you want to suppress duplicate error messages on the same field.
   *
   * @param propertyName - propertyName you want to test on
   * @param errorConstant - errorConstant you want to test
   * @return returns True if the propertyName indicated already has the errorConstant indicated, false otherwise
   *
   */
  protected boolean errorAlreadyExists(String propertyName, String errorConstant) {

      if (GlobalVariables.getMessageMap().fieldHasMessage(propertyName, errorConstant)) {
          return true;
      }
      else {
          return false;
      }
  }

  /**
   *
   * This method specifically doesn't put any prefixes before the error so that the developer can do things specific to the
   * globals errors (like newDelegateChangeDocument errors)
   *
   * @param propertyName
   * @param errorConstant
   */
  protected void putGlobalsError(String propertyName, String errorConstant) {
      if (!errorAlreadyExists(propertyName, errorConstant)) {
          GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(propertyName, errorConstant);
      }
  }

  /**
   *
   * This method specifically doesn't put any prefixes before the error so that the developer can do things specific to the
   * globals errors (like newDelegateChangeDocument errors)
   *
   * @param propertyName
   * @param errorConstant
   * @param parameter
   */
  protected void putGlobalsError(String propertyName, String errorConstant, String parameter) {
      if (!errorAlreadyExists(propertyName, errorConstant)) {
          GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(propertyName, errorConstant, parameter);
      }
  }

}
