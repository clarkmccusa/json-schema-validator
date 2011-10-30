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

package eel.kitchen.jsonschema.v2.instance;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

final class ArrayInstance
    implements Instance
{
    private final String pathElement;
    private final JsonNode node;
    private Set<Instance> children;

    ArrayInstance(final String pathElement, final JsonNode node)
    {
        this.node = node;
        this.pathElement = pathElement;

        children = new LinkedHashSet<Instance>();

        int i = 0;

        for (final JsonNode element: node)
            children.add(InstanceFactory.buildInstance(Integer.toString(i++),
                element));

        children = Collections.unmodifiableSet(children);
    }

    @Override
    public JsonNode getRawInstance()
    {
        return node;
    }

    @Override
    public NodeType getType()
    {
        return NodeType.ARRAY;
    }

    @Override
    public String getPathElement()
    {
        return pathElement;
    }

    @Override
    public String getAbsolutePath()
    {
        //TODO: implement
        return null;
    }

    @Override
    public Iterator<Instance> iterator()
    {
        return children.iterator();
    }
}