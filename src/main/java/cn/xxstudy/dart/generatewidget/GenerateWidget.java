package cn.xxstudy.dart.generatewidget;

import cn.xxstudy.dart.generatewidget.dialog.GenerateDartWidgetDialog;
import cn.xxstudy.dart.generatewidget.generate.DartGenerator;
import com.intellij.ide.IdeView;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;

public class GenerateWidget extends AnAction implements GenerateDartWidgetDialog.Listener {
    private DataContext dataContext;

    @Override
    public void actionPerformed(AnActionEvent e) {
        boolean ok = new GenerateDartWidgetDialog(this).showAndGet();
        if(ok){
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        dataContext = e.getDataContext();
        e.getPresentation().setEnabled(true);
    }

    @Override
    public void onGenerateClicked(String className, boolean createStateful) {
        DartGenerator dartGenerator = new DartGenerator(className, createStateful);
        generate(dartGenerator);
    }

    private void generate(DartGenerator dartGenerator) {
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        PsiDirectory directory = view.getOrChooseDirectory();
        ApplicationManager.getApplication().runWriteAction(() ->
                CommandProcessor.getInstance().executeCommand(project,
                        () -> createSourceFile(project, dartGenerator, directory),
                        "Generate dart", null));

    }

    private void createSourceFile(Project project, DartGenerator generator, PsiDirectory directory) {
        String fileName = generator.fileName();
        PsiFile existingPsiFile = directory.findFile(fileName);
        if (existingPsiFile != null) {
            Document document = PsiDocumentManager.getInstance(project).getDocument(existingPsiFile);
            document.insertString(document.getTextLength(), "\n" + generator.generate());
            return;
        }
        PsiFile psiFile = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName, JavaLanguage.INSTANCE, generator.generate());
        directory.add(psiFile);

    }

}
