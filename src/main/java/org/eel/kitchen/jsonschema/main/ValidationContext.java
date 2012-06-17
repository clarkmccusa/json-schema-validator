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
import org.eel.kitchen.util.CollectionUtils;

import java.util.Set;

public final class ValidationContext
{
    private final JsonResolver resolver;
    private final JsonSchemaFactory factory;

    private SchemaNode schemaNode;

    public ValidationContext(final JsonResolver resolver,
        final JsonSchemaFactory factory, final SchemaNode schemaNode)
    {
        this.resolver = resolver;
        this.factory = factory;
        this.schemaNode = schemaNode;
    }

    public ValidationReport validate(final JsonNode instance)
    {
        final ValidationReport ret = new ValidationReport();

        try {
            schemaNode = resolver.resolve(schemaNode);
        } catch (JsonSchemaException e) {
            ret.addMessage(e.getMessage());
            return ret;
        }

        final JsonSchema schema = factory.createFromNode(schemaNode.getNode());

        schema.validate(ret, instance);

        if (!ret.isSuccess())
            return ret;

        if (!instance.isContainerNode())
            return ret;

        final SchemaContainer container = schemaNode.getContainer();

        if (instance.isArray()) {
            int index = 0;
            JsonNode node;

            for (final JsonNode element: instance) {
                node = schemaNode.getArraySchema(index++);
                schemaNode = new SchemaNode(container, node);
               ret.mergeWith(validate(element));
            }

        } else { // object
            final Set<String> fieldNames
                = CollectionUtils.toSet(instance.fieldNames());
            Set<JsonNode> propertySchemas;

            for (final String field: fieldNames) {
                propertySchemas = schemaNode.getObjectSchemas(field);
                for (final JsonNode node: propertySchemas) {
                    schemaNode = new SchemaNode(container, node);
                    ret.mergeWith(validate(instance.get(field)));
                }
            }
        }

        return ret;
    }
}
