package org.codeProcessing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.madeforall.graph.Graph;
import org.madeforall.graph.NodeValueListener;

/**
 * Class which manages the code conversion
 * 
 * @author Samuel Teuber
 * @version 1.0
 */
public class WorkflowControl {
    public List<ProcessingActivity> activitiesOrdered;
    public ProcessingDataCollection data;
    private final Graph<ProcessingActivity> dependencyGraph;

    public WorkflowControl(final List<String> inputFile) {
        activitiesOrdered = new LinkedList<ProcessingActivity>();
        data = new ProcessingDataCollection(inputFile);
        this.dependencyGraph = new Graph<ProcessingActivity>(new NodeValueListener<ProcessingActivity>() {
            public void evaluating(final ProcessingActivity nodeValue) {
                activitiesOrdered.add(nodeValue);
            }
        });
    }

    public void addActivity(final ProcessingActivity activity) {
        this.addDependencies(activity);
    }

    public List<String> execute() {
        this.dependencyGraph.generateDependencies();
        final Iterator<ProcessingActivity> activityIterator = activitiesOrdered.iterator();
        while (activityIterator.hasNext()) {
            this.data = activityIterator.next().processDataObject(this.data);
            /*final List<String> file = this.data.getFile();
            final Iterator<String> fileIterator = file.iterator();
            while (fileIterator.hasNext()) {
                System.out.println(fileIterator.next());
            }*/
        }
        final List<String> file = this.data.getFile();
        final Iterator<String> fileIterator = file.iterator();
        while (fileIterator.hasNext()) {
            System.out.println(fileIterator.next());
        }
        return this.data.getFile();
    }

    private void addDependencies(final ProcessingActivity curActivity) {
        final List<ProcessingActivity> dependencies = curActivity.getPrerequisits();
        if (dependencies != null) {
            final Iterator<ProcessingActivity> dependencyIterator = dependencies.iterator();
            while (dependencyIterator.hasNext()) {
                final ProcessingActivity dependency = dependencyIterator.next();
                this.dependencyGraph.addDependency(dependency, curActivity);
                addDependencies(dependency);
            }
        }
    }
}
