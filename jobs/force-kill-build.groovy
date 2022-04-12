Jenkins.instance.getItemByFullName("Folder/job")
    .getBuildByNumber(42)
    .finish(hudson.model.Result.ABORTED, new java.io.IOException("Manual killed"));
