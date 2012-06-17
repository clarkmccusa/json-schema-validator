/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.ref.JsonResolver;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;

public final class JsonValidator
{
    private final JsonResolver resolver;
    private final JsonSchemaFactory factory;
    private final SchemaContainer container;

    public JsonValidator(final JsonResolver resolver,
        final JsonSchemaFactory factory, final SchemaContainer container)
    {
        this.resolver = resolver;
        this.factory = factory;
        this.container = container;
    }

    public ValidationReport validate(final JsonNode instance)
    {
        return validate("", instance);
    }

    private ValidationReport validate(final String fragment,
        final JsonNode instance)
    {
        final ValidationReport report = new ValidationReport();

        final SchemaNode schemaNode;
        try {
            schemaNode = container.lookupFragment(fragment);
        } catch (JsonSchemaException e) {
            report.addMessage(e.getMessage());
            return report;
        }

        final ValidationContext ctx = new ValidationContext(resolver,
            factory, schemaNode);

        return ctx.validate(instance);
    }
}
