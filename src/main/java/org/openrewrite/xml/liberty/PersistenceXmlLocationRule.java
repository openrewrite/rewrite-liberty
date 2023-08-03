/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.xml.liberty;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.marker.JavaProject;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PersistenceXmlLocationRule extends Recipe {

    @Override
    public String getDisplayName() {
        return "Move persistence.xml file.";
    }

    @Override
    public String getDescription() {
        return "This recipes moves persistence.xml files into the root META-INF directory in source folder.";
    }

    public File getProjectDirectory(File sourceFile, String projectName) {
        File parent = sourceFile.getParentFile();
        while(parent != null && !parent.getName().equals(projectName)) {
            parent = parent.getParentFile();
        }

        return parent;
    }

    public List<File> getSrcDirectories(SourceFile sourceFile) {
        List<File> srcDirs = new ArrayList<File>();
        Path sourcePath = sourceFile.getSourcePath();
        
        String projectName = sourceFile.getMarkers()
                            .findFirst(JavaProject.class)
                            .map(JavaProject::getProjectName)
                            .orElse("");
        
        File projectDirctory = getProjectDirectory(new File(sourcePath.toAbsolutePath().toString()), projectName);
        if(projectDirctory != null) {
            File[] subDirs = projectDirctory.listFiles(File::isDirectory);
            if(subDirs != null) {
                for (File subDir: subDirs) {
                    String dirName = subDir.getName().toLowerCase();
                    if(dirName.endsWith("src") || dirName.equals("source")) {
                        srcDirs.add(subDir);
                    }
                }
            }
        }

        return srcDirs;
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new TreeVisitor<Tree, ExecutionContext>() {
            @Nullable
            @Override
            public Tree visit(@Nullable Tree tree, ExecutionContext executionContext) {
                if (tree instanceof SourceFile) {
                    SourceFile sourceFile = ((SourceFile) tree);

                    Path sourcePath = ((SourceFile) tree).getSourcePath();
                    if(sourcePath.getFileName().toString().equals("persistence.xml")) {
                        String projectName = sourceFile.getMarkers()
                            .findFirst(JavaProject.class)
                            .map(JavaProject::getProjectName)
                            .orElse("");

                        List<File> srcDirs = getSrcDirectories(sourceFile);
                        boolean isValidPath = false;
                        boolean correctFileExists = false;
                        Path correctPath = null;
                        if(!srcDirs.isEmpty()) {
                            for(File srcDir: srcDirs) {
                                correctPath = srcDir.toPath().resolve("META-INF").resolve("persistence.xml");
                                if(sourcePath.toAbsolutePath().equals(correctPath.toAbsolutePath())) {
                                    isValidPath = true;
                                    break;
                                }

                                if(correctPath.toFile().exists()) {
                                    correctFileExists = true;
                                }
                            }
                        } else {
                            if(!sourcePath.toAbsolutePath().endsWith("src"+File.separator+"META-INF"+File.separator+"persistence.xml")) {
                                File projectFile = getProjectDirectory(new File(sourcePath.toAbsolutePath().toString()), projectName);
                                correctPath = projectFile.toPath().resolve("src").resolve("META-INF").resolve("persistence.xml");
                                return ((SourceFile) tree).withSourcePath(correctPath);
                            }
                        }

                        if(!isValidPath && !correctFileExists && correctPath != null) {
                            return ((SourceFile) tree).withSourcePath(correctPath);
                        } else {
                            return sourceFile;
                        }
                    }
                }
                return super.visit(tree, executionContext);
            }
        };
    }
}