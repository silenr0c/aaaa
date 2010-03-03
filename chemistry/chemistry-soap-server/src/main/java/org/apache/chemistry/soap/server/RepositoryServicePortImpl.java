/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.soap.server;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.WebServiceContext;

import org.apache.chemistry.ListPage;
import org.apache.chemistry.Paging;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.RepositoryManager;
import org.apache.chemistry.Type;
import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisExtensionType;
import org.apache.chemistry.ws.CmisPropertyDefinitionType;
import org.apache.chemistry.ws.CmisRepositoryCapabilitiesType;
import org.apache.chemistry.ws.CmisRepositoryEntryType;
import org.apache.chemistry.ws.CmisRepositoryInfoType;
import org.apache.chemistry.ws.CmisTypeContainer;
import org.apache.chemistry.ws.CmisTypeDefinitionListType;
import org.apache.chemistry.ws.CmisTypeDefinitionType;
import org.apache.chemistry.ws.ObjectFactory;
import org.apache.chemistry.ws.RepositoryServicePort;

import sun.security.pkcs11.Secmod.DbMode;

@WebService(name = "RepositoryServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
serviceName = "RepositoryService", //
portName = "RepositoryServicePort", //
endpointInterface = "org.apache.chemistry.ws.RepositoryServicePort")
public class RepositoryServicePortImpl implements RepositoryServicePort {

    private static final ObjectFactory factory = new ObjectFactory();

    @Resource
    private WebServiceContext wscontext;

    public List<CmisRepositoryEntryType> getRepositories(
            CmisExtensionType extension) throws CmisException {
        Collection<RepositoryEntry> repos = RepositoryManager.getInstance().getRepositories();
        List<CmisRepositoryEntryType> entries = new ArrayList<CmisRepositoryEntryType>(
                repos.size());
        for (RepositoryEntry repo : repos) {
            CmisRepositoryEntryType entry = factory.createCmisRepositoryEntryType();
            entry.setRepositoryId(repo.getId());
            entry.setRepositoryName(repo.getName());
            entries.add(entry);
        }
        return entries;
    }

    public CmisRepositoryInfoType getRepositoryInfo(String repositoryId,
            CmisExtensionType extension) throws CmisException {
        Repository repo = RepositoryManager.getInstance().getRepository(
                repositoryId);
        if (repo == null) {
            return null; // TODO or fault?
        }

        RepositoryInfo info = repo.getInfo();

        return ChemistryHelper.convert(info);
    }

    public CmisTypeDefinitionType getTypeDefinition(String repositoryId,
            String typeId, CmisExtensionType extension) throws CmisException {
        Repository repo = RepositoryManager.getInstance().getRepository(
                repositoryId);
        if (repo == null) {
            return null; // TODO or fault?
        }

        Type type = repo.getType(typeId);

        return ChemistryHelper.convert(type);
    }

    public CmisTypeDefinitionListType getTypeChildren(String repositoryId,
            String typeId, Boolean includePropertyDefinitions,
            BigInteger maxItems, BigInteger skipCount,
            CmisExtensionType extension) throws CmisException {
        Repository repo = RepositoryManager.getInstance().getRepository(
                repositoryId);
        if (repo == null) {
            return null; // TODO or fault?
        }

        boolean ipd = Boolean.TRUE.equals(includePropertyDefinitions);
        int mi = maxItems == null ? -1 : maxItems.intValue();
        int sc = skipCount == null ? -1 : skipCount.intValue();
        Paging paging = new Paging(mi, sc);

        ListPage<Type> types = repo.getTypeChildren(typeId, ipd, paging);

        return ChemistryHelper.convert(types);
    }

    public List<CmisTypeContainer> getTypeDescendants(String repositoryId,
            String typeId, BigInteger depth,
            Boolean includePropertyDefinitions, CmisExtensionType extension)
            throws CmisException {
        Repository repo = RepositoryManager.getInstance().getRepository(
                repositoryId);
        if (repo == null) {
            return null; // TODO or fault?
        }

        boolean ipd = Boolean.TRUE.equals(includePropertyDefinitions);
        int d = depth == null ? -1 : depth.intValue();

        Collection<Type> ctl = repo.getTypeDescendants(typeId, d, ipd);

        return ChemistryHelper.convert(ctl);
    }

}
