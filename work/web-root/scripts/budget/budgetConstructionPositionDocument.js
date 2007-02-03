/*
 * Copyright 2007 The Kuali Foundation.
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
function updateBCPositionFTE( bcPositionField ) {
	// we want the base label 
    var elPrefix = findElPrefix( bcPositionField.name );
	var positionStandardHoursDefault = getElementValue( elPrefix + ".positionStandardHoursDefault" ).trim();
	var iuNormalWorkMonths = getElementValue( elPrefix + ".iuNormalWorkMonths" ).trim();
	var iuPayMonths = getElementValue( elPrefix + ".iuPayMonths" ).trim();
	
	var positionFTEFieldName = elPrefix + ".positionFullTimeEquivalency";
	if ( positionStandardHoursDefault != "" && iuNormalWorkMonths != "" && iuPayMonths != "" ) {
		loadKualiObjectWithCallback( function( responseText ) {
			setRecipientValue( positionFTEFieldName, responseText );
		}, "positionFTE", positionStandardHoursDefault, iuNormalWorkMonths, iuPayMonths, "", "", "", "" );
	} else 
		clearRecipients( positionFTEFieldName );
}
