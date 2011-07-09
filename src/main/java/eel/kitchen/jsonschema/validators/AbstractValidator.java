/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractValidator
    implements Validator
{
    protected final JsonNode schemaNode;
    protected final List<String> validationErrors = new LinkedList<String>();
    protected final JsonNode EMPTY_SCHEMA;

    AbstractValidator(final JsonNode schemaNode)
    {
        this.schemaNode = schemaNode;

        final ObjectMapper mapper = new ObjectMapper();
        try {
            EMPTY_SCHEMA = mapper.readTree("{}");
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize validator", e);
        }
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
    }

    @Override
    public List<JsonNode> getSchemasForPath(final String subPath)
    {
        return Collections.emptyList();
    }

    @Override
    public final List<String> getValidationErrors()
    {
        return Collections.unmodifiableList(validationErrors);
    }

}