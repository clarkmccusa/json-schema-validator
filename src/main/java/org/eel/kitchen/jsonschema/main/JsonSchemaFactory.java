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
import com.fasterxml.jackson.databind.util.LRUMap;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.schema.KeywordFactory;
import org.eel.kitchen.jsonschema.schema.SyntaxValidator;

import java.util.Map;
import java.util.Set;

public final class JsonSchemaFactory
{
    private final Map<JsonNode, JsonSchema> cache
        = new LRUMap<JsonNode, JsonSchema>(15, 50);

    private final SyntaxValidator syntaxValidator;
    private final KeywordFactory keywordFactory;

    public JsonSchemaFactory(final SyntaxValidator syntaxValidator,
        final KeywordFactory keywordFactory)
    {
        this.syntaxValidator = syntaxValidator;
        this.keywordFactory = keywordFactory;
    }

    public synchronized JsonSchema createFromNode(final JsonNode node)
    {
        if (cache.containsKey(node))
            return cache.get(node);

        final JsonSchema ret;

        final ValidationReport report = checkSyntax(node);

        if (!report.isSuccess()) {
            ret = new InvalidJsonSchema(report.getMessages());
            cache.put(node, ret);
            return ret;
        }

        final Set<KeywordValidator> set = keywordFactory.getValidators(node);
        ret = new ValidJsonSchema(set);
        cache.put(node, ret);
        return ret;
    }

    private ValidationReport checkSyntax(final JsonNode node)
    {
        final ValidationReport ret = new ValidationReport();

        syntaxValidator.validate(ret, node);
        return ret;
    }
}
