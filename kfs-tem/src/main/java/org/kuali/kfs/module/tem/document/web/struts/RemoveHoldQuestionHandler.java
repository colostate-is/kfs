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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.replace;
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_HOLD_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.HOLD_TA_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.REMOVE_HOLD_NOTE_PREFIX;
import static org.kuali.kfs.module.tem.TemConstants.REMOVE_HOLD_TA_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.REMOVE_HOLD_TA_TEXT;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_REASON_PASTLIMIT;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_REASON_REQUIRED;
import static org.kuali.kfs.module.tem.TemKeyConstants.TA_MESSAGE_REMOVE_HOLD_DOCUMENT;
import static org.kuali.kfs.module.tem.TemKeyConstants.TA_QUESTION_DOCUMENT;
import static org.kuali.kfs.sys.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.sys.KFSConstants.MAPPING_BASIC;
import static org.kuali.kfs.sys.KFSConstants.NOTE_TEXT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME;

import java.util.ArrayList;

import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RemoveHoldQuestionHandler implements QuestionHandler<TravelDocument> {
    protected ConfigurationService ConfigurationService;
    protected DataDictionaryService dataDictionaryService;
    protected TravelDocumentService travelDocumentService;
    protected DocumentService documentService;
    protected DocumentDao documentDao;
    protected NoteService noteService;

    @Override
    public <T> T handleResponse(final Inquisitive<TravelDocument,?> asker) throws Exception {
        if (asker.denied(REMOVE_HOLD_TA_QUESTION)) {
            return (T) asker.back();
        }

        // Have to check length on value entered.
        String introNoteMessage = REMOVE_HOLD_NOTE_PREFIX + BLANK_SPACE;
        // Build out full message.
        final StringBuilder noteText = new StringBuilder(introNoteMessage + asker.getReason());

        int noteTextLength = noteText.length();
        // Get note text max length from DD.
        int noteTextMaxLength = getDataDictionaryService().getAttributeMaxLength(Note.class, NOTE_TEXT_PROPERTY_NAME).intValue();
        if (isBlank(asker.getReason()) || (noteTextLength > noteTextMaxLength)) {
            // Figure out exact number of characters that the user can enter.
            int reasonLimit = noteTextMaxLength - noteTextLength;
            reasonLimit = reasonLimit<0?reasonLimit*-1:reasonLimit;
            String message = getMessageFrom(TA_QUESTION_DOCUMENT);
            String question = replace(message, "{0}", REMOVE_HOLD_TA_TEXT);
            if (isBlank(asker.getReason())){
                return (T) asker.confirm(REMOVE_HOLD_TA_QUESTION, question, true, ERROR_TA_REASON_REQUIRED,QUESTION_REASON_ATTRIBUTE_NAME,REMOVE_HOLD_TA_TEXT);
            }
            else {
                return (T) asker.confirm(REMOVE_HOLD_TA_QUESTION, question, true, ERROR_TA_REASON_PASTLIMIT, QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
            }
        }

        final String messageType = TA_MESSAGE_REMOVE_HOLD_DOCUMENT;
        final TravelDocument document = asker.getDocument();

        String previousDocumentId = null;
        try {
            // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'
            T returnActionForward = null;
            returnActionForward = (T) ((StrutsInquisitor) asker).getMapping().findForward(MAPPING_BASIC);

            final Note newNote = getDocumentService().createNoteFromDocument(document, noteText.toString());
            document.addNote(newNote);
            getNoteService().save(newNote); // document dao doesn't save notes, so we need to save separately

            //save the new state on the document
             document.updateAndSaveAppDocStatus(TravelAuthorizationStatusCodeKeys.OPEN_REIMB);
            getDocumentDao().save(document);

            //send FYI for to initiator and traveler
            getTravelDocumentService().addAdHocFYIRecipient(document,document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
            getTravelDocumentService().addAdHocFYIRecipient(document,document.getTraveler().getPrincipalId());

            SpringContext.getBean(WorkflowDocumentService.class).sendWorkflowNotification(document.getDocumentHeader().getWorkflowDocument(), null, new ArrayList<AdHocRouteRecipient>(document.getAdHocRoutePersons()));

            if (ObjectUtils.isNotNull(returnActionForward)) {
                return returnActionForward;
            }
            else {
                return (T) asker.confirm(CONFIRM_HOLD_QUESTION, getMessageFrom(messageType), true, "temSingleConfirmationQuestion", HOLD_TA_QUESTION, "");
            }
        }
        catch (ValidationException ve) {
            throw ve;
        }

    }

    @Override
    public <T> T askQuestion(final Inquisitive<TravelDocument,?> asker) throws Exception {
        final String reason   = asker.getReason();
        final String key      = getMessageFrom(TA_QUESTION_DOCUMENT);
        final String question = replace(key, "{0}", REMOVE_HOLD_TA_TEXT);

        T retval = (T) asker.confirm(REMOVE_HOLD_TA_QUESTION, question, true);
        return retval;
    }



    public String getMessageFrom(final String messageType) {
        return getConfigurationService().getPropertyValueAsString(messageType);
    }

    /**
     * Sets the ConfigurationService attribute.
     *
     * @return Returns the ConfigurationService.
     */
    public void setConfigurationService(final ConfigurationService ConfigurationService) {
        this.ConfigurationService = ConfigurationService;
    }

    /**
     * Gets the ConfigurationService attribute.
     *
     * @return Returns the ConfigurationService.
     */
    protected ConfigurationService getConfigurationService() {
        return ConfigurationService;
    }

    /**
     * Sets the dataDictionaryService attribute.
     *
     * @return Returns the dataDictionaryService.
     */
    public void setDataDictionaryService(final DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the dataDictionaryService attribute.
     *
     * @return Returns the dataDictionaryService.
     */
    protected DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public DocumentDao getDocumentDao() {
        return documentDao;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

}
