package org.seedstack.i18n.rest.internal.translation;

import org.seedstack.business.Service;
import org.seedstack.i18n.internal.domain.model.key.Key;

import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@Service
public interface TranslationLocaleAssembler {

    TranslationRepresentation assemble(Key key, String locale);

    List<TranslationRepresentation> assemble(List<Key> key, String locale);
}
