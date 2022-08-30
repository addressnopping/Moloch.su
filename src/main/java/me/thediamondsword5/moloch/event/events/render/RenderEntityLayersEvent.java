package me.thediamondsword5.moloch.event.events.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.spartanb312.base.event.EventCenter;

public class RenderEntityLayersEvent extends EventCenter {
    public RenderLivingBase renderLivingBase;
    public ModelBase modelBase;
    public EntityLivingBase entityIn;
    public float limbSwing;
    public float limbSwingAmount;
    public float partialTicks;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;
    public float scaleIn;



    public RenderEntityLayersEvent(RenderLivingBase renderLivingBase, ModelBase modelBase, EntityLivingBase entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
        this.renderLivingBase = renderLivingBase;
        this.modelBase = modelBase;
        this.entityIn = entityIn;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.partialTicks = partialTicks;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scaleIn = scaleIn;
    }
}
