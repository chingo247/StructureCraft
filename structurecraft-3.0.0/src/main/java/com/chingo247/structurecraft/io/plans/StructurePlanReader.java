/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.structurecraft.io.plans;

import com.chingo247.structurecraft.io.placement.PlacementSource;
import com.chingo247.structurecraft.model.plans.StructurePlan;
import com.chingo247.structurecraft.io.xml.StructurePlanXMLConstants;
import com.chingo247.structurecraft.io.xml.ElementValueException;
import com.chingo247.structurecraft.exception.placement.PlacementNotRegisteredException;
import com.chingo247.structurecraft.io.placement.PlacementRegister;
import com.chingo247.structurecraft.io.placement.XMLElement;
import com.chingo247.structurecraft.io.util.PlacementElement;
import com.chingo247.structurecraft.io.xml.StructurePlanDocument;
import com.google.common.base.Preconditions;
import fiber.core.impl.xml.located.LocatedElement;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class StructurePlanReader {

    private static final Logger LOG = Logger.getLogger(StructurePlanReader.class.getName());

    public List<StructurePlan> readDirectory(File structurePlanDirectory) {
        Iterator<File> fit = FileUtils.iterateFiles(structurePlanDirectory, new String[]{"xml"}, true);
        
        ForkJoinPool pool = new ForkJoinPool();
        List<StructurePlan> plans = new ArrayList<>();
        try {
            Map<String, StructurePlanProcessor> processors = new HashMap<>();

            while (fit.hasNext()) {
                File structurePlanFile = fit.next();
                StructurePlanProcessor spp = new StructurePlanProcessor(structurePlanFile);
                processors.put(structurePlanFile.getAbsolutePath(), spp);
                pool.execute(spp);
            }
            
            for (StructurePlanProcessor spp : processors.values()) {
                StructurePlan plan = spp.get();
                if (plan != null) {
                    plans.add(plan);
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
        } finally {
            pool.shutdown();
        }
        return plans;
    }

    public StructurePlan loadFile(File structurePlanFile) {
        StructurePlanProcessor spp = new StructurePlanProcessor(structurePlanFile);
        return spp.compute();
    }

    /**
     *
     * @author Chingo
     */
    private class StructurePlanProcessor extends RecursiveTask<StructurePlan> {

        private File structurePlanFile;
        private StructurePlan parent;

        StructurePlanProcessor(File structurePlan) {
            this(structurePlan, null);
        }

        StructurePlanProcessor(File structurePlan, StructurePlan parent) {
            Preconditions.checkNotNull(structurePlan);
            Preconditions.checkArgument(structurePlan.exists());
            this.structurePlanFile = structurePlan;
            this.parent = parent;
        }

        @Override
        protected StructurePlan compute() {
            StructurePlanDocument planDocument = null;
            try {
                planDocument = StructurePlanDocument.read(structurePlanFile);
                planDocument.checkNotNull(StructurePlanXMLConstants.STRUCTURE_PLAN_PLACEMENT);

                String name = planDocument.getName();
                double price = planDocument.getPrice();
                String description = planDocument.getDescription();
                String category = planDocument.getCategory();
                PlacementElement placementElement = planDocument.getPlacementElement();
                PlacementProcessor placementProcessor = new PlacementProcessor(placementElement);
                placementProcessor.fork();

//                if (planDocument.hasSubStructureElements()) {
//
//                    DefaultSubstructuresPlan plan = new DefaultSubstructuresPlan(id, structurePlanFile, parent, placementProcessor.get());
//                    plan.setName(name);
//                    plan.setPrice(price);
//                    plan.setDescription(description);
//                    plan.setCategory(category);
//
//                    
//                    // Get and Set subplaceables
//                    List<SubStructureElement> substructureElements = planDocument.getSubStructureElements();
//                    List<StructurePlanProcessor> spps = null;
//                    List<PlacementProcessor> pps = null;
//                    if (!substructureElements.isEmpty()) {
//                        spps = new ArrayList<>();
//                        pps = new ArrayList<>();
//
//                        for (SubStructureElement subStructureElement : substructureElements) {
//                            String t = subStructureElement.getType();
//
//                            if (t.trim().toLowerCase().equals("embedded")) {
//                                // Perform recursion check here!
//                                // Fully check branch for matchin types!
//                                File f = handleEmbeddedPlan(structurePlanFile, subStructureElement);
//                                if (plan.matchesParentRecursively(f)) {
//                                    throw new PlanException("Element <" + subStructureElement.getElementName() + "> on line " + subStructureElement.getLine()
//                                            + " matches a plan in his branch!");
//                                }
//                                spps.add(new StructurePlanProcessor(f, parent));
//                            } else {
//                                pps.add(new PlacementProcessor(structurePlanFile.getParentFile(), placementElement));
//                            }
//                        }
//                    }
//
//                    // Fork the processes
//                    if (pps != null) {
//                        for (PlacementProcessor pp : pps) {
//                            pp.fork();
//                        }
//                    }
//                    if (spps != null) {
//                        for (StructurePlanProcessor spp : spps) {
//                            spp.fork();
//                        }
//                    }
//
//                    // Collect the data
//                    if (pps != null) {
//                        for (PlacementProcessor pp : pps) {
//                            plan.addPlacement(pp.get());
//                        }
//                    }
//                    if (spps != null) {
//                        for (StructurePlanProcessor spp : spps) {
//                            plan.addStructurePlan(spp.get());
//                        }
//                    }
                    // Recursive process SubStructurePlans
//                    if (parent == null) {
//                StructurePlanUtil.validate(plan);
//                    }
//                    LOG.print(LogLevel.INFO, structurePlanFile, "StructurePlan", System.currentTimeMillis() - start);
//                    return plan;
//                } else {
                StructurePlan plan = new StructurePlan(structurePlanFile, name, category, price, placementProcessor.get());
                plan.setName(name);
                plan.setPrice(price);
                plan.setDescription(description);
                plan.setCategory(category);
//                    LOG.print(LogLevel.INFO, structurePlanFile, "StructurePlan", System.currentTimeMillis() - start);
                return plan;
//                }

            } catch (ElementValueException ex) {
                LOG.log(Level.SEVERE, "Error in ''{0}'' >>\n{1}", new Object[]{structurePlanFile.getAbsolutePath(), ex.getMessage()});
            } catch (DocumentException | InterruptedException | ExecutionException ex) {
                java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
            }

            return null;
        }

//        private File handleEmbeddedPlan(File structurePlan, SubStructureElement element) throws DocumentException, ElementValueException {
//            element.checkNotNull("File");
//
//            LineElement simpleElement = element.selectSingleElement("File");
//            simpleElement.checkNotEmpty();
//
//            File f = new File(structurePlan.getParent(), element.getTextValue());
//            if (!f.exists()) {
//                throw new PlanException("File reference '" + f.getAbsolutePath() + "' defined on line " + simpleElement.getLine() + " does not exist!");
//            }
//            if (!isStructurePlan(f)) {
//                throw new PlanException("File is not a valid StructurePlan file");
//            }
//            return f;
//        }
        private boolean isStructurePlan(File file) throws DocumentException {
            SAXReader reader = new SAXReader();
            Document d = reader.read(file);
            return d.getRootElement().getName().equals("StructurePlan");
        }

    }

    private class PlacementProcessor extends RecursiveTask<PlacementSource> {

        private final PlacementElement placeableNode;

        /**
         * Processes a Placeable
         *
         * @param placementNode
         */
        public PlacementProcessor(PlacementElement placementNode) {
            this.placeableNode = placementNode;
        }

        @Override
        protected PlacementSource compute() {
            try {
                XMLElement element = new XMLElement(placeableNode.getFile(), placeableNode.getElement());

                // Try deserializing it...
                PlacementRegister.IMP.deserialize(element);
                
                String plugin, type; 
                if (element.getElement().attribute("plugin") != null && element.getElement().attribute("type") != null) {
                    LocatedElement le = element.getElement();
                    plugin = le.attributeValue("plugin");
                    type = le.attributeValue("type");
                } else {
                    Node pluginNode = element.getElement().selectSingleNode("Plugin");
                    Node typeNode = element.getElement().selectSingleNode("Type");
                    plugin = pluginNode != null ? pluginNode.getText() : null;
                    type = typeNode != null ? typeNode.getText() : null;
                }

                return new PlacementSource(element, plugin, type);

            } catch (PlacementNotRegisteredException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
            return null;
        }

    }

}
