package xyz.dogboy.agriseasons;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class AgriSeasonsTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!"com.infinityraider.agricraft.tiles.TileEntityCrop".equals(transformedName)) {
            return basicClass;
        }

        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        this.insertGrowthTickHook(classNode);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);

        return classWriter.toByteArray();
    }

    private void insertGrowthTickHook(ClassNode classNode) {
        MethodNode onGrowthTickMethod = classNode.methods.stream().filter(method -> "onGrowthTick".equals(method.name)).findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to find onGrowthTick method in TileEntityCrop"));

        InsnList toInsert = new InsnList();
        LabelNode label = new LabelNode(new Label());
        toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
        toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "xyz/dogboy/agriseasons/AgriSeasonsHelper", "onGrowthTickPre",
                "(Lcom/infinityraider/agricraft/tiles/TileEntityCrop;)Z", false));
        toInsert.add(new JumpInsnNode(Opcodes.IFEQ, label));
        toInsert.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/infinityraider/agricraft/api/v1/util/MethodResult", "PASS",
                "Lcom/infinityraider/agricraft/api/v1/util/MethodResult;"));
        toInsert.add(new InsnNode(Opcodes.ARETURN));
        toInsert.add(label);
        toInsert.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

        onGrowthTickMethod.instructions.insert(toInsert);
    }

}
