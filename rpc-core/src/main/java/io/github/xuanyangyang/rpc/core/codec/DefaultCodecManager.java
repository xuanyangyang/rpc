package io.github.xuanyangyang.rpc.core.codec;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认codec管理
 *
 * @author xuanyangyang
 * @since 2020/10/6 14:42
 */
public class DefaultCodecManager implements CodecManager {
    /**
     * codec Id -> codec
     */
    private final Map<Short, Codec> codecMap = new HashMap<>();

    @Override
    public Codec getCodec(Short id) {
        return codecMap.get(id);
    }

    @Override
    public boolean addCodec(Codec codec) {
        if (codec == null) {
            return false;
        }
        codecMap.put(codec.getId(), codec);
        return true;
    }

    @Override
    public Codec removeCodec(Short id) {
        return codecMap.remove(id);
    }

    @Override
    public boolean hasCodec(Short id) {
        return codecMap.containsKey(id);
    }
}
