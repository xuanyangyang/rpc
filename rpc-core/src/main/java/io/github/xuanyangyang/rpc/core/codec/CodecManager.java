package io.github.xuanyangyang.rpc.core.codec;

/**
 * codec管理
 *
 * @author xuanyangyang
 * @since 2020/10/4 18:17
 */
public interface CodecManager {
    Codec getCodec(Short id);
}
