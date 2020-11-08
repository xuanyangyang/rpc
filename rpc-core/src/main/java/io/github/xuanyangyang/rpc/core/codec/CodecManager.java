package io.github.xuanyangyang.rpc.core.codec;

/**
 * codec管理
 *
 * @author xuanyangyang
 * @since 2020/10/4 18:17
 */
public interface CodecManager {
    /**
     * 通过ID获取 codec
     *
     * @param id codec id
     * @return codec
     */
    Codec getCodec(Short id);

    /**
     * 添加codec
     *
     * @param codec codec
     * @return 是否添加成功
     */
    boolean addCodec(Codec codec);

    /**
     * 移除codec
     *
     * @param id codec id
     * @return 被移除的codec
     */
    Codec removeCodec(Short id);

    /**
     * @param id codec id
     * @return 是否有ID对应的codec
     */
    boolean hasCodec(Short id);
}
