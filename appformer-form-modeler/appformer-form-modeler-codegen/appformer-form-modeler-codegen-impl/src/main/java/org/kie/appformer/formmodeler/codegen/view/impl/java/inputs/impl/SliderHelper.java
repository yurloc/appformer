/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.appformer.formmodeler.codegen.view.impl.java.inputs.impl;

import org.kie.appformer.formmodeler.codegen.SourceGenerationContext;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.SliderBaseDefinition;

public class SliderHelper extends AbstractInputCreatorHelper<SliderBaseDefinition> {

    @Override
    public String getInputWidget( SliderBaseDefinition fieldDefinition ) {
        /*
            Patch class to avoid error descrived on: https://github.com/gwtproject/gwt/issues/9242
            TODO: change it when fixed
        */
        return "org.kie.workbench.common.forms.common.rendering.client.widgets.slider.Slider";
    }

    @Override
    public String getReadonlyMethod( String fieldName, String readonlyParam ) {
        return fieldName + ".setEnabled( !" + readonlyParam + ");";
    }

    @Override
    public String getInputInitLiteral( SourceGenerationContext context, SliderBaseDefinition field ) {
        return "new Slider( "
                + field.getMin().doubleValue() + ", "
                + field.getMax().doubleValue() + ", "
                + field.getPrecision().doubleValue() + ", "
                + field.getStep().doubleValue()
                + " );";
    }


    @Override
    public String getSupportedFieldTypeCode() {
        return SliderBaseDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public boolean isInputInjectable() {
        return false;
    }
}